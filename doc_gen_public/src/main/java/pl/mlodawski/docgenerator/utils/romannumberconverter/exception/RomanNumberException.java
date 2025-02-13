package pl.mlodawski.docgenerator.utils.romannumberconverter.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */

/**
 * RomanNumberException is a custom exception used to handle errors specifically related
 * to Roman numeral conversions.
 *
 * This exception extends the BaseException class and automatically associates
 * it with the HTTP status code INTERNAL_SERVER_ERROR, ensuring a consistent error
 * response when thrown in a web application context.
 *
 * Key Characteristics:
 * - Provides a meaningful error message that describes the issue encountered during
 *   Roman numeral conversion operations.
 * - Used predominantly in the RomanNumberService class to signify input validation
 *   or conversion process errors.
 *
 * Constructor Details:
 * RomanNumberException(String message):
 * - Accepts a descriptive error message that provides context for the exception.
 * - Automatically assigns the HTTP status INTERNAL_SERVER_ERROR.
 */
public class RomanNumberException extends BaseException {
    public RomanNumberException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
