package pl.mlodawski.docgenerator.docmanager.searchtext.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * MissingRequiredValueException is a custom exception that indicates a required value
 * is missing during the application's runtime.
 *
 * This exception extends the BaseException class and is specifically associated with
 * HTTP conflict status (HTTP 409), signaling that the current state of the resource conflicts
 * with an expected condition due to missing required values.
 *
 * Key Features:
 * - Intended for scenarios where important data or parameters are absent during processing.
 * - Extends the BaseException, leveraging its ability to represent a detailed message
 *   and HTTP status.
 * - Sets the HTTP status to HttpStatus.CONFLICT for consistent error reporting.
 *
 * Typical Usage:
 * Use this exception to signal that a request or operation cannot proceed as intended
 * because a required value is missing.
 */
public class MissingRequiredValueException extends BaseException {

    public MissingRequiredValueException(String message) {
        super(message, HttpStatus.CONFLICT);

    }

}
