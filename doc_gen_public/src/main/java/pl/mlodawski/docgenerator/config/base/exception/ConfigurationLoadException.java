package pl.mlodawski.docgenerator.config.base.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * Exception class for handling errors that occur during the process of loading
 * application configuration data.
 *
 * This exception is typically thrown when the application encounters a failure
 * while attempting to load the configuration, such as issues with file I/O,
 * parsing errors in the configuration content, or unexpected data formats.
 *
 * The exception message provides a description of the error encountered, and
 * the cause helps to capture the underlying exception for additional context.
 * It extends the BaseException class with the HTTP status code set to
 * INTERNAL_SERVER_ERROR, indicating a server-side issue during configuration
 * loading.
 */
public class ConfigurationLoadException extends BaseException {
    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}
