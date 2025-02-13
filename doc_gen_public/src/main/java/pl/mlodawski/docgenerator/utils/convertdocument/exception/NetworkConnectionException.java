package pl.mlodawski.docgenerator.utils.convertdocument.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * NetworkConnectionException is a specific exception that extends {@link BaseException}.
 *
 * This exception is designed to handle errors related to failures in network connections.
 * It is initialized with a default HTTP status of {@code HttpStatus.INTERNAL_SERVER_ERROR},
 * signifying a server-side issue during network communication processes.
 *
 * Key Features:
 * - Enables passing a custom error message to clearly describe the network-related problem.
 * - Accepts a {@link Throwable} cause to encapsulate the underlying exception that triggered this error.
 * - Inherits behavior and properties from {@link BaseException}, including the associated HTTP status.
 *
 * Suitable for use cases where network communication fails, and detailed error reporting
 * is necessary, particularly in web-based or service-oriented applications.
 */

public class NetworkConnectionException extends BaseException {
    public NetworkConnectionException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
