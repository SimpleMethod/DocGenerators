package pl.mlodawski.docgenerator.config.base.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * Exception indicating that a required configuration file was not found.
 *
 * This exception is used to handle scenarios where the application
 * attempts to load a configuration file from a specified path, but
 * the file does not exist. It extends the BaseException class and
 * provides NOT_FOUND as the associated HTTP status code.
 *
 * The exception message includes the path of the missing configuration
 * file, providing additional context for diagnosing the issue.
 */
public class ConfigurationNotFoundException extends BaseException {
    public ConfigurationNotFoundException(String path) {
        super(STR."Configuration file not found at: \{path}", HttpStatus.NOT_FOUND);
    }
}
