package pl.mlodawski.docgenerator.config.dragonfly;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pl.mlodawski.docgenerator.config.dragonfly.exception.DragonflyConnectionException;
import pl.mlodawski.docgenerator.core.manager.configuration.DragonflyConfigurationManager;


import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 @Author Michał Młodawski
 */
/**
 * Configuration class for managing Dragonfly connectivity using Lettuce client.
 * The class is responsible for creating, validating, and managing the lifecycle
 * of the Redis connection factory and template.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DragonflyConfiguration {
    private final DragonflyConfigurationManager configManager;
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private ClientResources clientResources;
    private LettuceConnectionFactory connectionFactory;

    /**
     * Creates and configures a RedisConnectionFactory with retry mechanism.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        DragonflyConfig config = configManager.getConfiguration();
        clientResources = DefaultClientResources.builder().build();

        for (int attempt = 1; attempt <= config.getMaxRetryAttempts(); attempt++) {
            try {
                return createAndValidateConnectionFactory(config);
            } catch (Exception e) {
                handleConnectionAttemptFailure(attempt, config, e);
                if (attempt == config.getMaxRetryAttempts()) {
                    return createFailsafeConnectionFactory();
                }
                sleepBetweenRetries(config.getRetryDelayMs());
            }
        }
        return createFailsafeConnectionFactory();
    }

    /**
     * Creates and configures a RedisTemplate for key-value operations.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       RedisSerializer<Object> springSessionDefaultRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(springSessionDefaultRedisSerializer);
        template.setValueSerializer(springSessionDefaultRedisSerializer);
        return template;
    }

    private LettuceConnectionFactory createAndValidateConnectionFactory(DragonflyConfig config) {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(config.getHost());
        redisConfig.setPort(config.getPort());
        redisConfig.setDatabase(config.getDatabase());

        if (StringUtils.isNotBlank(config.getPassword())) {
            redisConfig.setPassword(RedisPassword.of(config.getPassword()));
        }

        LettucePoolingClientConfiguration clientConfig = createClientConfiguration(config);

        connectionFactory = new LettuceConnectionFactory(redisConfig, clientConfig);
        connectionFactory.afterPropertiesSet();

        // Validate connection
        try {
            connectionFactory.getConnection().ping();
            log.info("Successfully established Redis connection to {}:{}", config.getHost(), config.getPort());
            return connectionFactory;
        } catch (Exception e) {
            if (connectionFactory != null) {
                connectionFactory.destroy();
            }
            throw new DragonflyConnectionException("Failed to validate Redis connection: " + e.getMessage());
        }
    }

    private LettucePoolingClientConfiguration createClientConfiguration(DragonflyConfig config) {
        return LettucePoolingClientConfiguration.builder()
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .build())
                .clientResources(clientResources)
                .commandTimeout(Duration.ofMillis(config.getConnectionTimeoutMs()))
                .poolConfig(createPoolConfig(config))
                .build();
    }

    private org.apache.commons.pool2.impl.GenericObjectPoolConfig<?> createPoolConfig(DragonflyConfig config) {
        org.apache.commons.pool2.impl.GenericObjectPoolConfig<?> poolConfig =
                new org.apache.commons.pool2.impl.GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(config.getMaxPoolSize());
        poolConfig.setMinIdle(config.getMinPoolSize());
        poolConfig.setMaxIdle(config.getMaxPoolSize());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        return poolConfig;
    }

    private void handleConnectionAttemptFailure(int attempt, DragonflyConfig config, Exception e) {
        if (attempt == config.getMaxRetryAttempts()) {
            log.error("Failed to connect to Redis after {} attempts", attempt, e);
        } else {
            log.warn("Redis connection attempt {} of {} failed. Retrying in {} seconds...",
                    attempt, config.getMaxRetryAttempts(), config.getRetryDelayMs()/1000, e);
        }
    }

    private void sleepBetweenRetries(long delayMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(delayMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new DragonflyConnectionException("Connection retry interrupted");
        }
    }

    private LettuceConnectionFactory createFailsafeConnectionFactory() {
        return new LettuceConnectionFactory() {
            @Override
            public void afterPropertiesSet() {

            }
        };
    }

    @PreDestroy
    public void destroy() {
        if (isShuttingDown.compareAndSet(false, true)) {
            gracefulShutdown();
        }
    }

    private void gracefulShutdown() {
        log.info("Starting graceful shutdown of Redis connections...");
        if (connectionFactory != null) {
            try {
                connectionFactory.destroy();
            } catch (Exception e) {
                log.error("Error closing Redis connection factory", e);
            }
        }
        if (clientResources != null) {
            try {
                clientResources.shutdown();
            } catch (Exception e) {
                log.error("Error shutting down Redis client resources", e);
            }
        }
        log.info("Redis connections cleanup completed");
    }
}
