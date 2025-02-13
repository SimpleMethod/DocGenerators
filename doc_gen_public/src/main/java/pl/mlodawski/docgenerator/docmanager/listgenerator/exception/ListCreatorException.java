package pl.mlodawski.docgenerator.docmanager.listgenerator.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * ListCreatorException is a custom exception that occurs during operations related to the creation
 * or processing of lists within the application.
 *
 * This exception extends the BaseException, allowing it to encapsulate both a detailed error
 * message and an HTTP status code for standardized error reporting in a web-based context.
 * It facilitates debugging by optionally including the message of a root cause if provided.
 *
 * Key Characteristics:
 * - Accepts a descriptive error message to clarify the context of the exception.
 * - Optionally includes the cause message of an underlying exception, if available.
 * - Automatically associates the exception with an "Internal Server Error" HTTP status to
 *   indicate an error originating from the server during list-related processes.
 *
 * Intended Use:
 * This exception is typically used to signal errors during operations where list creation,
 * manipulation, or validation processes encounter issues.
 */
public class ListCreatorException extends BaseException {

    public ListCreatorException(String message, Throwable cause) {
        super(STR."\{message}\{cause != null ? STR.": \{cause.getMessage()}" : ""}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
