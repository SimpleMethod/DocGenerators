package pl.mlodawski.docgenerator.config.app.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * Represents an exception that is thrown when there is an issue with the application configuration.
 *
 * This exception extends {@code BaseException}, inheriting its ability to associate an HTTP
 * status code with the exception. By default, it uses the HTTP status {@code INTERNAL_SERVER_ERROR}.
 *
 * Typical use cases for this exception include:
 * - Errors encountered during reading or parsing the application configuration.
 * - Issues with invalid application configuration values.
 *
 * The provided message parameter allows specifying detailed information about the
 * configuration error, which can help in diagnosing issues.
 */
public class AppConfigurationException extends BaseException {
    public AppConfigurationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}