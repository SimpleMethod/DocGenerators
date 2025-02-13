package pl.mlodawski.docgenerator.config.base.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * Exception class for representing errors encountered during the validation
 * of application configurations.
 *
 * This exception is used to indicate that a validation error has occurred
 * while processing configuration data. It inherits from the BaseException
 * and uses the HTTP status code INTERNAL_SERVER_ERROR to signify a server-side
 * issue.
 *
 * The exception message contains a detailed description of the validation
 * issue that caused the failure.
 */
public class ConfigurationValidationException extends BaseException {
    public ConfigurationValidationException(String message) {
        super(STR."Configuration validation failed: \{message}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
