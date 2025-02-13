package pl.mlodawski.docgenerator.core.manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;
import pl.mlodawski.docgenerator.config.app.AppConfig;
import pl.mlodawski.docgenerator.config.app.exception.AppConfigurationException;


import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
 @Author Michał Młodawski
 */
/**
 * Manages the application-specific configurations using the base configuration
 * manager framework. This class handles the initialization, validation, and management
 * of the application-specific configuration, extending the default behavior of
 * the base configuration manager.
 *
 * Responsibilities:
 * - Loads the application configuration from the default configuration path.
 * - Validates and enforces correctness of configuration attributes such as timezone
 *   and locale.
 * - Provides a mechanism to generate a default configuration if no configuration is
 *   found at the default path.
 *
 * Key Features:
 * - Timezone validation ensures all timezone configurations conform to standard
 *   {@code ZoneId} formats.
 * - Locale validation ensures that all locale configurations adhere to the
 *   standard "language_REGION" format.
 * - Supports logging of configuration loading and validation processes.
 *
 * Error Handling:
 * This class will throw an {@link AppConfigurationException} in the event of invalid
 * configuration data, errors during the creation of default configuration, or
 * an invalid timezone or locale.
 */

@Slf4j
@Component
public class AppConfigurationManager extends BaseConfigurationManager<AppConfig> {
    private static final String DEFAULT_CONFIG_PATH = "config/app-config.json";
    private static final String DEFAULT_TIMEZONE = "UTC";
    private static final String DEFAULT_LOCALE = "en_US";
    private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;
    private static final String DEFAULT_FRONTEND_URL = "http://localhost:5173";
    private static final String DEFAULT_DOCX_TO_PDF_CONVERTER_URL = "http://localhost:8082";
    private static final List<String> DEFAULT_ALLOWED_ORIGINS = Arrays.asList(
            "http://127.0.0.1:8081",
            "http://localhost:8081"
    );

    /**
     * Manages the application-specific configuration, initializes and validates
     * the configuration from the default path or creates a default configuration
     * if it doesn't exist.
     *
     * @param objectMapper the ObjectMapper instance used for reading and writing JSON configurations
     * @param validator the Validator instance used for validating the configuration objects
     */
    @Autowired
    public AppConfigurationManager(ObjectMapper objectMapper, jakarta.validation.Validator validator) {
        super(objectMapper, validator, DEFAULT_CONFIG_PATH, AppConfig.class);
    }

    /**
     * Creates a default configuration for the application.
     * Constructs a new instance of {@link AppConfig} with predefined values for timezone, locale, and log level.
     * Logs the successful creation of the default configuration details, including the configured values.
     * If an error occurs during the configuration creation process, an {@link AppConfigurationException} is thrown.
     *
     * @return an instance of {@link AppConfig} representing the default configuration.
     * @throws AppConfigurationException if the default configuration cannot be created.
     */
    @Override
    protected AppConfig createDefaultConfig() {
        try {
            AppConfig config = AppConfig.builder()
                    .timezone(validateTimezone(DEFAULT_TIMEZONE))
                    .locale(DEFAULT_LOCALE)
                    .logLevel(DEFAULT_LOG_LEVEL)
                    .frontendUrl(DEFAULT_FRONTEND_URL)
                    .allowedOrigins(DEFAULT_ALLOWED_ORIGINS)
                    .docxToPdfConverterUrl(DEFAULT_DOCX_TO_PDF_CONVERTER_URL)

                    .build();

            log.info("Created default configuration with timezone: {}, locale: {}, logLevel: {}, frontendUrl: {}, allowedOrigins: {}, docxToPdfConverterUrl: {}",
                    config.getTimezone(), config.getLocale(), config.getLogLevel(),
                    config.getFrontendUrl(), config.getAllowedOrigins(),
                    config.getDocxToPdfConverterUrl());


            return config;
        } catch (Exception e) {
            throw new AppConfigurationException("Failed to create default configuration");
        }
    }

    /**
     * Validates if the provided timezone string represents a valid timezone according to {@code ZoneId}.
     * If the timezone is valid, it is returned unchanged. Otherwise, an exception is thrown.
     *
     * @param timezone the timezone string to validate
     * @return the same timezone string if it is valid
     * @throws AppConfigurationException if the timezone is invalid
     */
    private String validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return timezone;
        } catch (Exception e) {
            throw new AppConfigurationException("Invalid timezone: " + timezone);
        }
    }

    /**
     * Validates the provided application configuration and checks for correct timezone
     * and locale values.
     *
     * @param config the configuration instance to validate
     * @throws AppConfigurationException if the configuration contains invalid timezone
     *         or locale values
     */
    @Override
    protected void validateConfiguration(AppConfig config) {
        if (!isValidTimezone(config.getTimezone())) {
            throw new AppConfigurationException("Invalid timezone configuration");
        }

        if (!isValidLocale(config.getLocale())) {
            throw new AppConfigurationException("Invalid locale configuration");
        }

        if (!isValidUrl(config.getFrontendUrl())) {
            throw new AppConfigurationException("Invalid frontend URL configuration");
        }

        if (!areValidOrigins(config.getAllowedOrigins())) {
            throw new AppConfigurationException("Invalid allowed origins configuration");
        }
    }

    /**
     * Validates a list of origins to ensure all entries are valid URLs.
     *
     * The method checks if the provided list of origins is not null or empty,
     * and then verifies that all entries in the list are valid URLs using the
     * {@code isValidUrl} helper method.
     *
     * @param origins a list of String values representing origins to be validated
     * @return true if the list is not null, not empty, and all entries are valid URLs; false otherwise
     */
    private boolean areValidOrigins(List<String> origins) {
        if (origins == null || origins.isEmpty()) {
            return false;
        }
        return origins.stream().allMatch(this::isValidUrl);
    }

    /**
     * Verifies whether the provided URL string is valid. A valid URL is a non-null string
     * that starts with "http://" or "https://".
     *
     * @param url the URL string to be validated
     * @return true if the URL is valid, false otherwise
     */
    private boolean isValidUrl(String url) {
        return url != null && url.matches("^(http|https)://.*$");
    }

    /**
     * Validates whether the provided timezone string corresponds to a valid ZoneId.
     *
     * @param timezone the timezone string to validate
     * @return true if the timezone string is valid, false otherwise
     */
    private boolean isValidTimezone(String timezone) {
        try {
            return ZoneId.of(timezone) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates the provided locale string against the format expected by the application.
     * A valid locale string must have a language and region separated by an underscore
     * (e.g., "en_US") and must successfully construct a Locale object.
     *
     * @param locale the locale string to be validated
     * @return true if the locale string is valid, false otherwise
     */
    private boolean isValidLocale(String locale) {
        try {
            String[] parts = locale.split("_");
            if (parts.length != 2) {
                return false;
            }
            new Locale.Builder()
                    .setLanguage(parts[0])
                    .setRegion(parts[1])
                    .build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}