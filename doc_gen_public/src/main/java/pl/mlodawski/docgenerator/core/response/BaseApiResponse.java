package pl.mlodawski.docgenerator.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/*
 @Author Michał Młodawski
 */
/**
 * Represents a base structure for an API response.
 *
 * @param <T> the type of data contained in the response
 * @param data the response data
 * @param message an optional message providing context about the response
 * @param status the HTTP status associated with the response
 * @param error optional error details, provided in case of an error response
 * @param timestamp the time of response creation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseApiResponse<T>(T data, String message, HttpStatus status,
                                 ErrorDetails error, Instant timestamp) {

    
    public record ErrorDetails(String code, String detail) {
    }

    /**
     * Creates a successful API response with the specified data.
     *
     * @param data the data to include in the response
     * @return a {@code BaseApiResponse} object containing the provided data and an HTTP status of OK
     */
    public static <T> BaseApiResponse<T> success(T data) {
        return new BaseApiResponse<>(data, null, HttpStatus.OK, null, Instant.now());
    }

    /**
     * Creates an error response with the specified message and HTTP status.
     *
     * @param message the error message to include in the response
     * @param status the HTTP status to associate with the error response
     * @return a {@code BaseApiResponse} object containing the error details and specified HTTP status
     */
    public static <T> BaseApiResponse<T> error(String message, HttpStatus status) {
        return new BaseApiResponse<>(null, message, status,
                new ErrorDetails(
                        status.name(),
                        message
                ), Instant.now());
    }

    public static <T> BaseApiResponse<T> error(String message) {
        return new BaseApiResponse<>(null, message, HttpStatus.BAD_GATEWAY,
                new ErrorDetails(
                        HttpStatus.BAD_GATEWAY.name(),
                        message
                ), Instant.now());
    }
}