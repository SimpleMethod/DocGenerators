package pl.mlodawski.docgenerator.config.dragonfly;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.events.AbstractSessionEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import java.time.Duration;

/*
 @Author Michał Młodawski
 */
@Configuration
@EnableRedisHttpSession(redisNamespace = "horizon")
@Slf4j
public class DragonflySessionConfig {
    /**
     * Configures and provides a default Redis serializer to serialize objects to JSON using Jackson.
     * This method customizes the `ObjectMapper` to ensure compatibility with security modules,
     * Java time module, and handles deserialization features appropriately.
     *
     * @return a `RedisSerializer` configured with a `GenericJackson2JsonRedisSerializer`
     *         using the customized `ObjectMapper` for serialization and deserialization.
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        objectMapper.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }


    /**
     * Creates and configures a {@link RedisIndexedSessionRepository} for managing sessions in a Redis datastore.
     * The session repository is configured with a specific namespace and default maximum inactive interval.
     *
     * @param redisTemplate the RedisTemplate used for Redis operations. It is injected as a dependency.
     * @param maxInactiveIntervalInSeconds the maximum time, in seconds, for which a session can remain inactive
     *                                      before it is considered expired. This value is configurable via the
     *                                      "spring.session.timeout" property with a default value of 3600 seconds.
     * @return a configured instance of {@link RedisIndexedSessionRepository} for handling session persistence.
     */
    @Bean
    public RedisIndexedSessionRepository redisIndexedSessionRepository(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.session.timeout:3600}") int maxInactiveIntervalInSeconds) {

        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(redisTemplate);
        repository.setDefaultMaxInactiveInterval(Duration.ofSeconds(maxInactiveIntervalInSeconds));
        repository.setRedisKeyNamespace("horizon");

        return repository;
    }




    /**
     * Creates a bean of type {@code ApplicationListener<AbstractSessionEvent>} that listens for session events and
     * handles session destruction to remove the session information from the {@link SessionRegistry}.
     *
     * @param sessionRegistry the {@link SessionRegistry} that holds session information, used to remove session details
     *                        upon session destruction.
     * @return an {@code ApplicationListener} that listens for session events and responds specifically to
     *         {@link SessionDestroyedEvent} by removing the destroyed session's information from the registry.
     */
    @Bean
    public ApplicationListener<AbstractSessionEvent> sessionEventListener(SessionRegistry sessionRegistry) {
        return event -> {
            if (event instanceof SessionDestroyedEvent) {
                String sessionId = event.getSessionId();
                sessionRegistry.removeSessionInformation(sessionId);
                log.debug("Removed session from registry on destroy: {}", sessionId);
            }
        };
    }

    /**
     * Creates a {@link SpringSessionBackedSessionRegistry} bean that uses a {@link RedisIndexedSessionRepository}
     * as the underlying session repository. This registry allows integration with Spring Security
     * session management and provides session manipulation methods.
     *
     * @param sessionRepository the {@link RedisIndexedSessionRepository} to be used as the session backing repository.
     * @return a {@link SessionRegistry} backed by the provided {@link RedisIndexedSessionRepository}.
     */
    @Bean
    public SessionRegistry springSessionBackedSessionRegistry(RedisIndexedSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

    /**
     * Registers a {@link HttpSessionEventPublisher} bean, which is responsible for publishing
     * HTTP session-related events such as session creation and destruction. This allows
     * for integration with Spring's event handling mechanism.
     *
     * @return an instance of {@link HttpSessionEventPublisher}
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}