package pl.mlodawski.docgenerator.config.dragonfly.exception;

import org.springframework.http.HttpStatus;
import pl.mlodawski.docgenerator.core.exception.BaseException;

/*
 @Author Michał Młodawski
 */
/**
 * RedisConnectionException represents an exception that occurs when a
 * Redis connection fails or encounters an unexpected issue.
 */
public class DragonflyConnectionException extends BaseException {
    public DragonflyConnectionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
