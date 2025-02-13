package pl.mlodawski.docgenerator.docmanager.imageinsert.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * ImageException is a custom exception that extends BaseException and is used to
 * handle errors related to image processing operations in the application.
 *
 * Purpose:
 * It is specifically designed to be thrown when issues occur, such as inserting
 * images into documents, headers, footers, or paragraphs.
 *
 * Features:
 * - Accepts a detailed error message to describe the cause of the issue.
 * - Optionally allows the inclusion of a Throwable instance to specify the root cause.
 * - Automatically sets the HTTP status code to INTERNAL_SERVER_ERROR (500),
 *   indicating a server-side failure.
 *
 * Constructor Parameters:
 * - `message`: A description of the exception scenario.
 * - `cause`: An optional Throwable object representing the underlying issue, if available.
 *
 * Usage:
 * This exception aims to standardize error handling for image-related tasks, making
 * it consistent with the application's error handling strategy via the BaseException class.
 */
public class ImageException extends BaseException {

    public ImageException(String message, Throwable cause) {
        super(STR."\{message}\{cause != null ? STR.": \{cause.getMessage()}" : ""}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
