package pl.mlodawski.docgenerator.core.exception;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pl.mlodawski.docgenerator.config.app.exception.AppConfigurationException;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationLoadException;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationNotFoundException;
import pl.mlodawski.docgenerator.config.base.exception.ConfigurationValidationException;
import pl.mlodawski.docgenerator.config.dragonfly.exception.DragonflyConnectionException;
import pl.mlodawski.docgenerator.config.usos.exception.UsosConfigurationException;
import pl.mlodawski.docgenerator.core.response.BaseApiResponse;

import pl.mlodawski.docgenerator.docmanager.imageinsert.exception.ImageException;
import pl.mlodawski.docgenerator.docmanager.listgenerator.exception.ListCreatorException;
import pl.mlodawski.docgenerator.docmanager.searchtext.exception.MissingRequiredValueException;
import pl.mlodawski.docgenerator.docmanager.searchtext.exception.ValueNotFoundException;
import pl.mlodawski.docgenerator.modulecardgenerator.exception.InvalidOperationInCalculationException;
import pl.mlodawski.docgenerator.usosmodule.exception.UsosApiException;
import pl.mlodawski.docgenerator.usosmodule.exception.UsosSessionException;
import pl.mlodawski.docgenerator.utils.convertdocument.exception.NetworkConnectionException;
import pl.mlodawski.docgenerator.utils.convertdocument.exception.RenderException;
import pl.mlodawski.docgenerator.utils.filemanager.exception.ResourceNotFoundException;
import pl.mlodawski.docgenerator.utils.generatearchive.exception.PackageZipArchiveException;
import pl.mlodawski.docgenerator.utils.romannumberconverter.exception.RomanNumberException;


import javax.naming.ConfigurationException;
import java.util.stream.Collectors;

/*
 @Author Michał Młodawski
 */

/**
 * GlobalExceptionHandler is a centralized exception handler to manage application-wide exceptions
 * in a structured way. It uses Spring's {@link RestControllerAdvice} to intercept and handle various
 * exception types at the controller layer.
 * <p>
 * This class improves the consistency and clarity of error responses by converting exceptions
 * into HTTP-compliant responses using {@link ResponseEntity}. For each handled exception type,
 * it provides a standardized structure for error responses using {@code BaseApiResponse}.
 * <p>
 * Features:
 * - Logs the details of exceptions using a logging framework.
 * - Provides specific error handling strategies for common exception types, such as validation and
 * request-related exceptions.
 * - Ensures clear and user-friendly error messages in HTTP responses for API clients.
 * <p>
 * List of Handled Exceptions:
 * <p>
 * - {@link MissingServletRequestParameterException}: Triggered when a required request parameter
 * is missing. Responds with a 400 BAD_REQUEST status.
 * <p>
 * - {@link MethodArgumentNotValidException}: Triggered when method argument validation fails.
 * Responds with a 400 BAD_REQUEST status and lists validation errors.
 * - {@link ConstraintViolationException}: Triggered when a violation of constraints in input data occurs.
 * Responds with a 400 BAD_REQUEST status and lists constraint violation errors.
 * - {@link ConfigurationException}: Triggered for configuration-related issues. Responds with a
 * 500 INTERNAL_SERVER_ERROR status.
 * <p>
 * Logging:
 * - Each exception handler logs the details of the exception to aid in debugging and monitoring.
 * - For validation-related exceptions, detailed messages including invalid fields are logged.
 * <p>
 * Error Response:
 * - The response body is generated using the {@link BaseApiResponse} utility method.
 * - The response structure includes: error message, HTTP status, error details, and timestamp.
 */
@Slf4j
@RestControllerAdvice
@Order(1)
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("Authorization denied error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseApiResponse.error("AUTHORIZATION_DENIED: " + ex.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("METHOD_ARGUMENT_TYPE_MISMATCH: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HTTP message not readable error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("HTTP_MESSAGE_NOT_READABLE: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("ILLEGAL_STATE: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseApiResponse.error("BAD_CREDENTIALS: " + ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HTTP request method not supported error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseApiResponse.error("METHOD_NOT_ALLOWED: " + ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("Resource not found error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseApiResponse.error("RESOURCE_NOT_FOUND: " + ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("INTERNAL_ERROR: An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        String errorMessage = String.format("Required parameter '%s' is missing", ex.getParameterName());
        log.error(errorMessage, ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error(errorMessage, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Validation failed: {}", errors, ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error(errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        log.error("Constraint violation: {}", errors, ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error(errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleConfigurationException(ConfigurationException ex) {
        log.error("Configuration error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }


    @ExceptionHandler(UsosSessionException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleSessionException(UsosSessionException ex) {
        log.error("Session error: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(BaseApiResponse.error("SESSION_ERROR: " + ex.getMessage(), ex.getStatus()));
    }

    @ExceptionHandler(UsosApiException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleApiException(UsosApiException ex) {
        log.error("API error: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(BaseApiResponse.error("API_ERROR: " + ex.getMessage(), ex.getStatus()));
    }

    @ExceptionHandler(RomanNumberException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleRomanNumberException(RomanNumberException ex) {
        log.error("Roman number error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("ROMAN_NUMBER_ERROR: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(PackageZipArchiveException.class)
    public ResponseEntity<BaseApiResponse<Void>> handlePackageZipArchiveException(PackageZipArchiveException ex) {
        log.error("Package zip archive error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("PACKAGE_ZIP_ARCHIVE_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseApiResponse.error("RESOURCE_NOT_FOUND: " + ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RenderException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleRenderException(RenderException ex) {
        log.error("Render error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("RENDER_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(NetworkConnectionException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleNetworkConnectionException(NetworkConnectionException ex) {
        log.error("Network connection error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("NETWORK_CONNECTION_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(InvalidOperationInCalculationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleInvalidOperationInCalculationException(InvalidOperationInCalculationException ex) {
        log.error("Invalid operation in calculation error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("INVALID_OPERATION_IN_CALCULATION: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ValueNotFoundException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleValueNotFoundException(ValueNotFoundException ex) {
        log.error("Value not found error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseApiResponse.error("VALUE_NOT_FOUND: " + ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MissingRequiredValueException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleMissingRequiredValueException(MissingRequiredValueException ex) {
        log.error("Missing required value error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("MISSING_REQUIRED_VALUE: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ListCreatorException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleListCreatorException(ListCreatorException ex) {
        log.error("List creator error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("LIST_CREATOR_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ImageException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleImageException(ImageException ex) {
        log.error("Image error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("IMAGE_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(UsosConfigurationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleUsosConfigurationException(UsosConfigurationException ex) {
        log.error("Usos configuration error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("USOS_CONFIGURATION_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(DragonflyConnectionException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleDragonflyConnectionException(DragonflyConnectionException ex) {
        log.error("Dragonfly connection error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("DRAGONFLY_CONNECTION_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ConfigurationValidationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleConfigurationValidationException(ConfigurationValidationException ex) {
        log.error("Configuration validation error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("CONFIGURATION_VALIDATION_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ConfigurationNotFoundException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleConfigurationNotFoundException(ConfigurationNotFoundException ex) {
        log.error("Configuration not found error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseApiResponse.error("CONFIGURATION_NOT_FOUND: " + ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ConfigurationLoadException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleConfigurationLoadException(ConfigurationLoadException ex) {
        log.error("Configuration load error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("CONFIGURATION_LOAD_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(AppConfigurationException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleAppConfigurationException(AppConfigurationException ex) {
        log.error("App configuration error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseApiResponse.error("APP_CONFIGURATION_ERROR: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BaseApiResponse<Void>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("Missing request header error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseApiResponse.error("MISSING_REQUEST_HEADER: " + ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

}