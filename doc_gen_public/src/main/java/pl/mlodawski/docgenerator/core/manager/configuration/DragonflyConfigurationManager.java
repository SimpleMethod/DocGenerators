package pl.mlodawski.docgenerator.core.manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mlodawski.docgenerator.config.dragonfly.DragonflyConfig;
import pl.mlodawski.docgenerator.config.dragonfly.exception.DragonflyConnectionException;

/*
 @Author Michał Młodawski
 */
/**
 * Manager class responsible for handling Dragonfly-specific configurations.
 */
@Slf4j
@Component
public class DragonflyConfigurationManager extends BaseConfigurationManager<DragonflyConfig> {
    private static final String DEFAULT_CONFIG_PATH = "config/dragonfly-config.json";

    @Autowired
    public DragonflyConfigurationManager(ObjectMapper objectMapper, Validator validator) {
        super(objectMapper, validator, DEFAULT_CONFIG_PATH, DragonflyConfig.class);
    }

    @Override
    protected DragonflyConfig createDefaultConfig() {
        return DragonflyConfig.builder()
                .host("localhost")
                .port(6379)
                .database(0)
                .password(null)
                .maxRetryAttempts(5)
                .retryDelayMs(10000)
                .connectionTimeoutMs(5000)
                .maxPoolSize(10)
                .minPoolSize(2)
                .build();
    }

    @Override
    protected void validateConfiguration(DragonflyConfig config) {
        super.validateConfiguration(config);
        validatePoolSizes(config);
    }

    private void validatePoolSizes(DragonflyConfig config) {
        if (config.getMinPoolSize() > config.getMaxPoolSize()) {
            throw new DragonflyConnectionException(
                    "Minimum pool size cannot be greater than maximum pool size");
        }
    }
}
