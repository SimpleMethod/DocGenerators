package pl.mlodawski.docgenerator.utils.convertdocument.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * RenderException is a specific exception that extends {@link BaseException}.
 *
 * This exception is designed to handle errors that occur during a rendering process.
 * It is initialized with a default HTTP status of {@code HttpStatus.INTERNAL_SERVER_ERROR},
 * indicating a server-side error during rendering operations.
 *
 * Key Features:
 * - Allows for passing a custom error message to describe the rendering issue.
 * - Accepts a {@link Throwable} cause to encapsulate the underlying exception that triggered this error.
 * - Inherits behavior and properties from {@link BaseException}, including an associated HTTP status.
 *
 * Suitable for scenarios involving rendering-specific failures where detailed error reporting
 * is needed, particularly in the context of web applications.
 */
public class RenderException extends BaseException {

    public RenderException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
