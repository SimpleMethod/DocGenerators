package pl.mlodawski.docgenerator.utils.filemanager.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
@Author Michał Młodawski
*/
/**
 * Exception thrown when a requested resource is not found.
 *
 * The {@code ResourceNotFoundException} is a runtime exception used to indicate
 * that a specific resource, such as a file, database entry, or other entity,
 * could not be located. This exception is typically used in the context of
 * operations where the absence of a required resource is considered an error
 * condition.
 *
 * Constructors:
 * - Allows specifying an error message to describe the nature of the missing
 *   resource.
 * - Optionally, supports chaining another {@code Throwable} cause to provide
 *   additional context for the exception.
 *
 * This exception can be used to signal client-side issues (such as invalid
 * input leading to a missing resource) or server-side issues (such as
 * corrupted or missing data).
 */
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(STR."\{message} \{cause.getMessage()}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}