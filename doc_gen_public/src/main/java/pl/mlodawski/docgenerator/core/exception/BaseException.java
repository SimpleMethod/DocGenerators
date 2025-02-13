package pl.mlodawski.docgenerator.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
 @Author Michał Młodawski
 */
/**
 * BaseException serves as a generalized exception type for custom application-specific exceptions.
 *
 * This class extends RuntimeException, providing the flexibility to define additional properties
 * or methods required for domain-specific exception handling. One such property includes an
 * HTTP status code, allowing for standardized mapping of exceptions to HTTP responses.
 *
 * Key Features:
 * - Stores an associated HTTP status code to represent the nature of the error in an HTTP context.
 * - Allows derived classes to supply detailed error messages and an appropriate HTTP status.
 * - Suitable for being extended by custom exceptions that require explicit handling in a web-based application.
 *
 * Usage:
 * Extend this class to create a specific exception type by providing a meaningful message and
 * an appropriate HTTP status code, enabling consistent exception handling and status reporting
 * across the application.
 *
 * Behavior:
 * - Utilizes the constructor to initialize the exception message and HTTP status code.
 * - Provides access to the associated HTTP status through the `getStatus()` method.
 */
@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}