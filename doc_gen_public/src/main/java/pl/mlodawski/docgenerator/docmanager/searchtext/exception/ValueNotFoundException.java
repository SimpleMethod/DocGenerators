package pl.mlodawski.docgenerator.docmanager.searchtext.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;


/*
 @Author Michał Młodawski
 */

/**
 * ValueNotFoundException is a custom exception that indicates a specific value could not be
 * found or resolved during processing.
 *
 * This exception extends the BaseException class, enabling the representation of an error
 * message along with an associated HTTP status code. It supports multiple constructors to
 * handle different levels of detail in describing the exception.
 *
 * Characteristics:
 * - Utilized to signal that a particular value is absent or cannot be located.
 * - Extends BaseException, allowing for consistent error handling and HTTP status representation.
 *
 * Constructors:
 * - The first constructor initializes the exception with a detailed message and sets the HTTP
 *   status to NOT_FOUND (404), suggesting the requested value is not available.
 * - The second constructor initializes the exception with a message and a cause, while setting
 *   the HTTP status to INTERNAL_SERVER_ERROR (500), indicating an unexpected server-side error
 *   related to the missing value.
 *
 * Typical Usage:
 * This exception can be thrown in scenarios where the application cannot proceed due to the
 * absence of a required value, such as missing data from an external source or an invalid
 * reference to a non-existent entity.
 */
public class ValueNotFoundException extends BaseException {
    public ValueNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ValueNotFoundException(String message, Throwable cause) {
        super(STR."\{message}\{cause != null ? STR.": \{cause.getMessage()}" : ""}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
