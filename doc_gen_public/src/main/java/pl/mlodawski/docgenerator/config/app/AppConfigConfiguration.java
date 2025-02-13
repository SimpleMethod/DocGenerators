package pl.mlodawski.docgenerator.config.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mlodawski.docgenerator.config.app.exception.AppConfigurationException;
import pl.mlodawski.docgenerator.core.manager.configuration.AppConfigurationManager;


import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/*
 @Author Michał Młodawski
 */
/**
 * AppConfigConfiguration is a Spring configuration class responsible for setting up and initializing
 * the application configuration. It manages properties such as timezone, locale, and log level,
 * and ensures that system settings are configured accordingly.
 *
 * This class interacts with an instance of {@code AppConfigurationManager} to load, validate,
 * and apply configuration settings. It provides a bean of type {@code AppConfig}
 * for dependency injection throughout the application.
 */
@Slf4j
@Configuration
public class AppConfigConfiguration {
    private final AppConfigurationManager appConfigurationManager;

    /**
     * Constructs an instance of AppConfigConfiguration.
     *
     * @param appConfigurationManager the manager responsible for handling application configuration operations.
     */
    public AppConfigConfiguration(AppConfigurationManager appConfigurationManager) {
        this.appConfigurationManager = appConfigurationManager;
    }

    /**
     * Initializes the application configuration by delegating the setup to the
     * {@code AppConfigurationManager}.*/
    @Bean
    public AppConfig appConfig() {
        try {
            appConfigurationManager.initialize();
            AppConfig config = appConfigurationManager.getConfiguration();
            validateConfiguration(config);

            configureSystem(config);

            return config;
        } catch (Exception e) {
            log.error("Failed to initialize application configuration", e);
            throw new AppConfigurationException("Application configuration initialization failed");
        }
    }

    /**
     * Validates the provided application configuration to ensure all required properties are properly defined.
     * Throws an exception if any property is invalid or the configuration is null.
     *
     * @param config the application configuration object to validate; must not be null.
     *               Includes settings for timezone, locale, and log level.
     * @throws AppConfigurationException if the configuration object is null or if any individual property is invalid.
     */
    private void validateConfiguration(AppConfig config) {
        if (config == null) {
            throw new AppConfigurationException("Configuration is null");
        }

        validateTimezone(config.getTimezone());
        validateLocale(config.getLocale());
        validateLogLevel(config.getLogLevel());
        validateUrl(config.getFrontendUrl());
        validateUrl(config.getDocxToPdfConverterUrl());
    }

    /**
     * Validates the provided URL based on specific conditions:
     * - The URL cannot be null or empty.
     * - The URL must start with "http://" or "https://".
     *
     * Throws an {@link AppConfigurationException} if the validation fails.
     *
     * @param url The URL to be validated.
     *            Must not be null, empty, or improperly formatted.
     */
    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new AppConfigurationException("Frontend URL cannot be null or empty");
        }
        if (!url.matches("^(http|https)://.*$")) {
            throw new AppConfigurationException("Frontend URL must start with http:// or https://");
        }
    }


    /**
     * Validates the provided timezone string to ensure it is neither null nor empty
     * and represents a valid timezone identifier.
     *
     * @param timezone the timezone string to validate; must not be null, empty, or invalid.
     * @throws AppConfigurationException if the provided timezone is null, empty, or not a valid timezone identifier.
     */
    private void validateTimezone(String timezone) {
        if (timezone == null || timezone.trim().isEmpty()) {
            throw new AppConfigurationException("Timezone cannot be null or empty");
        }
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new AppConfigurationException("Invalid timezone: " + timezone);
        }
    }

    /**
     * Validates the provided locale string for correct format and constructs a Locale object.
     *
     * The method ensures that the locale string follows the expected format "language_COUNTRY",
     * where "language" is a two-letter lowercase ISO 639-1 language code and "COUNTRY" is a
     * two-letter uppercase ISO 3166-1 alpha-2 country code. If the locale string is invalid,
     * it throws an {@code AppConfigurationException}.
     *
     * @param localeStr the locale string to validate, expected in the format "language_COUNTRY"
     * @throws AppConfigurationException if the locale string is null, empty, improperly formatted,
     *                                    or*/
    private void validateLocale(String localeStr) {
        if (localeStr == null || localeStr.trim().isEmpty()) {
            throw new AppConfigurationException("Locale cannot be null or empty");
        }
        try {
            String[] parts = localeStr.split("_");
            if (parts.length != 2) {
                throw new AppConfigurationException("Invalid locale format. Expected format: language_COUNTRY");
            }
            Locale.Builder localeBuilder = new Locale.Builder();
            localeBuilder.setLanguage(parts[0]);
            localeBuilder.setRegion(parts[1]);
            localeBuilder.build();
        } catch (Exception e) {
            throw new AppConfigurationException("Invalid locale: " + localeStr);
        }
    }

    /**
     * Validates the provided log level to ensure it is not null.
     *
     * @param logLevel the logging level to validate; must not be null.
     * @throws AppConfigurationException if the logLevel is null.
     */
    private void validateLogLevel(LogLevel logLevel) {
        if (logLevel == null) {
            throw new AppConfigurationException("Log level cannot be null");
        }
    }

    /**
     * Configures the system settings based on the provided application configuration.
     *
     * This method applies the system locale, timezone, and logging level based on the values
     * provided in the {@code AppConfig} object. If any step of the configuration process fails,
     * an {@code AppConfigurationException} is thrown.
     *
     * @param config the application configuration object containing the locale, timezone,
     *               and log level settings to be applied. The {@code config} parameter must
     *               not be null and should contain valid values as per the application's requirements.
     * @throws AppConfigurationException if the system properties cannot be configured due to invalid
     *                                    values or internal errors.
     */
    private void configureSystem(AppConfig config) {
        try {
            setSystemLocale(config.getLocale());
            setSystemTimezone(config.getTimezone());
            configureLogging(config.getLogLevel());

            System.setProperty("app.frontend.url", config.getFrontendUrl());

            log.info("System configuration applied successfully with frontend URL: {}", config.getFrontendUrl());
        } catch (Exception e) {
            throw new AppConfigurationException("Failed to configure system properties");
        }
    }

    /**
     * Sets the system locale based on the provided locale string.
     *
     * This method parses the given string into a language and region pair,
     * constructs a Locale object, and sets it as the default system locale.
     * Additionally, it updates the "app.locale" system property to reflect the new locale.
     * If any error occurs during parsing or setting the locale, an exception is thrown.
     *
     * @param localeStr the locale string in the format "language_COUNTRY" (e.g., "en_US").
     *                  The language is a two-letter lowercase ISO 639 code,
     *                  and the country is a two-letter uppercase ISO 3166 code.
     *                  If the format is invalid or cannot be processed, an exception is thrown.
     * @throws AppConfigurationException if the locale string is invalid or fails to be applied to the system.
     */
    private void setSystemLocale(String localeStr) {
        try {
            String[] parts = localeStr.split("_");
            Locale locale = new Locale.Builder()
                    .setLanguage(parts[0])
                    .setRegion(parts[1])
                    .build();
            Locale.setDefault(locale);
            System.setProperty("app.locale", localeStr);
            log.debug("System locale set to: {}", localeStr);
        } catch (Exception e) {
            throw new AppConfigurationException("Failed to set system locale: " + localeStr);
        }
    }

    /**
     * Sets the system timezone based on the provided timezone identifier.
     *
     * This method validates the provided timezone identifier, ensuring it is valid and recognized.
     * If valid, it updates the system's default timezone and the "user.timezone" system property.
     *
     * @param timezone the identifier of the timezone to be set (e.g., "Europe/Warsaw" or "GMT").
     *                 Must be a valid timezone identifier. If the identifier is invalid, an exception is thrown.
     * @throws AppConfigurationException if the specified timezone is invalid or fails to be applied to the system.
     */
    private void setSystemTimezone(String timezone) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(timezone);
            if (timeZone.getID().equals("GMT") && !timezone.equals("GMT")) {
                throw new AppConfigurationException("Invalid timezone identifier: " + timezone);
            }
            TimeZone.setDefault(timeZone);
            System.setProperty("user.timezone", timezone);
            log.debug("System timezone set to: {}", timezone);
        } catch (Exception e) {
            throw new AppConfigurationException("Failed to set system timezone: " + timezone);
        }
    }

    /**
     * Configures the logging level for the application using the provided log level.
     *
     * @param logLevel the desired logging level to be applied. Must be a valid instance of {@code LogLevel}.
     *                 This value determines the verbosity of the logging output, such as DEBUG, INFO, WARN, etc.
     *                 The method maps this log level to a corresponding Logback logging level.
     * @throws AppConfigurationException if the logging configuration fails due to any internal error.
     */
    private void configureLogging(LogLevel logLevel) {
        try {
            Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            Level level = mapLogLevel(logLevel);
            rootLogger.setLevel(level);
            log.debug("Logging level set to: {}", level);
        } catch (Exception e) {
            throw new AppConfigurationException("Failed to configure logging");
        }
    }

    /**
     * Maps the given {@code LogLevel} to the corresponding Logback {@code Level}.
     *
     * @param logLevel the Spring Boot {@code LogLevel} to be mapped to a Logback {@code Level}
     * @return the mapped Logback {@code Level} corresponding to the input {@code LogLevel}
     */
    private Level mapLogLevel(LogLevel logLevel) {
        return switch (logLevel) {
            case DEBUG -> Level.DEBUG;
            case ERROR, FATAL -> Level.ERROR;
            case INFO -> Level.INFO;
            case OFF -> Level.OFF;
            case TRACE -> Level.TRACE;
            case WARN -> Level.WARN;
        };
    }
}