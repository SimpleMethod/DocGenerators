package pl.mlodawski.docgenerator.utils.generatearchive.exception;


import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;



/**
 * PackageZipArchiveException represents an exception that occurs when there is an error
 * related to the processing of a ZIP archive for a package.
 *
 * This exception extends the BaseException class, leveraging the ability to associate
 * the error with an HTTP status code. By default, this exception uses the
 * HTTP status code INTERNAL_SERVER_ERROR to indicate a server-side problem when handling
 * the ZIP archive.
 *
 * Typical scenarios for this exception include:
 * - Errors during the creation or extraction of a ZIP archive.
 * - Corrupted or invalid ZIP file structures.
 * - Other ZIP processing-related issues.
 *
 * Usage:
 * This exception should be thrown in contexts where ZIP archive processing errors
 * need to be explicitly indicated and handled, particularly in a web application context
 * where HTTP status codes are relevant.
 *
 * Inherits Constructor:
 * The constructor allows passing a detailed error message and the underlying
 * cause (Throwable) for the exception. The associated HTTP status code
 * is predefined as INTERNAL_SERVER_ERROR.
 */ /*
 @Author Michał Młodawski
 */
public class PackageZipArchiveException extends BaseException {

    public PackageZipArchiveException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
