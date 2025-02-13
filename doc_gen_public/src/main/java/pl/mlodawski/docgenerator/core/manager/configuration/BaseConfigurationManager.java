package pl.mlodawski.docgenerator.core.manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationLoadException;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationNotFoundException;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationValidationException;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/*
 @Author Michał Młodawski
 */
/**
 * BaseConfigurationManager is an abstract class designed to manage the configuration
 * settings for an application. It provides functionalities to load, validate, and
 * manage application-specific configurations in a structured and robust manner.
 *
 * The class leverages dependency injection for object mapping and validation processes
 * and includes error handling for common configuration issues, such as file not found,
 * validation errors, and loading failures. It also supports the creation of default
 * configurations when no configuration file exists.
 *
 * This class is parameterized with a type {@code T}, representing the specific
 * configuration managed by its concrete implementations.
 *
 * @param <T> the type of configuration object managed by this class
 */
@Slf4j
public abstract class BaseConfigurationManager<T> {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    protected T configuration;
    private final String configPath;
    private final Class<T> configClass;

    /**
     * Constructs a BaseConfigurationManager instance to manage application-specific
     * configuration using the provided dependencies and configuration properties.
     *
     * @param objectMapper the ObjectMapper instance used to parse and serialize JSON configuration data
     * @param validator the Validator instance used to validate configuration objects
     * @param configPath the file path of the configuration file
     * @param configClass the Class type of the configuration being managed
     */
    @Autowired
    public BaseConfigurationManager(ObjectMapper objectMapper, Validator validator,
                                    String configPath, Class<T> configClass) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.configPath = configPath;
        this.configClass = configClass;
    }

    /**
     * Initializes the configuration, ensuring it is loaded and validated from the specified
     * configuration path. If the configuration file does not exist, a default configuration
     * is created. Logs the result of the initialization process.
     *
     * Error Handling:
     * - Throws a {@link ConfigurationLoadException} if an I/O error or unexpected error occurs during configuration loading.
     * - Throws a {@link ConfigurationNotFoundException} if the configuration file does not exist and default creation fails.
     * - Rethrows exceptions of type {@link ConfigurationValidationException} and {@link ConfigurationLoadException} encountered during validation or loading.
     *
     * Detailed Steps:
     * 1. Verifies if the configuration file exists at the specified path.
     * 2. Attempts to create a default configuration if the file does not exist.
     * 3. Loads and validates the configuration from the file.
     * 4. Logs the successful initialization process with the configuration path.
     *
     * @throws ConfigurationLoadException if an I/O error or unexpected error occurs during initialization.
     * @throws ConfigurationValidationException if an invalid configuration is found during validation.
     * @throws ConfigurationNotFoundException if the configuration cannot be found or created.
     */
    public void initialize() {
        try {
            Path path = Paths.get(configPath);
            if (!Files.exists(path)) {
                if (!createDefaultConfig(path)) {
                    throw new ConfigurationNotFoundException(configPath);
                }
            }

            configuration = loadAndValidateConfiguration(path);
            log.info("Successfully initialized configuration from: {}", path);
        } catch (IOException e) {
            throw new ConfigurationLoadException("IO error while loading configuration", e);
        } catch (Exception e) {
            if (e instanceof ConfigurationLoadException ||
                    e instanceof ConfigurationValidationException) {
                throw e;
            }
            throw new ConfigurationLoadException("Unexpected error while loading configuration", e);
        }
    }

    /**
     * Loads the configuration data from the specified file path using the configured object mapper
     * and validates the loaded configuration.
     *
     * @param path the file path to the configuration file to be loaded
     * @return the loaded and validated configuration object of type T
     * @throws IOException if an I/O error occurs while reading the file
     */
    private T loadAndValidateConfiguration(Path path) throws IOException {
        T config = objectMapper.readValue(path.toFile(), configClass);
        validateConfiguration(config);
        return config;
    }

    /**
     * Validates the provided configuration object against the defined constraints.
     * If the validation finds violations, a {@link ConfigurationValidationException} is thrown
     * with a detailed message containing all validation errors.
     *
     * @param config the configuration object to be validated
     * @throws ConfigurationValidationException if any constraint violations are found in the configuration
     */
    protected void validateConfiguration(T config) {
        Set<ConstraintViolation<T>> violations = validator.validate(config);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));

            throw new ConfigurationValidationException(errors);
        }
    }

    /**
     * Creates a default configuration instance for the implementing class.
     * This method is responsible for constructing and returning a default configuration
     * object with predefined settings or values specific to the concrete implementation.
     *
     * @return an instance of type {@code T} representing the default configuration.
     */
    protected abstract T createDefaultConfig();

    /**
     * Creates a default configuration file at the specified path.
     * If the configuration file already exists, it validates and writes the default configuration
     * to the provided path. The method ensures the parent directories for the file exist
     * before attempting to write the configuration.
     *
     * @param path the path where the default configuration file should be created
     * @return true if the default configuration file was successfully created, false otherwise
     */
    private boolean createDefaultConfig(Path path) {
        try {
            T defaultConfig = createDefaultConfig();
            validateConfiguration(defaultConfig);

            Files.createDirectories(path.getParent());
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(path.toFile(), defaultConfig);

            log.info("Created default configuration at: {}", path);
            return true;
        } catch (Exception e) {
            log.error("Failed to create default configuration at {}", path, e);
            return false;
        }
    }

    /**
     * Retrieves the current configuration instance. If the configuration has not been
     * initialized yet, it will invoke the `initialize` method to load and validate
     * the configuration.
     *
     * @return the current configuration instance of type T
     */
    public T getConfiguration() {
        if (configuration == null) {
            initialize();
        }
        return configuration;
    }
}