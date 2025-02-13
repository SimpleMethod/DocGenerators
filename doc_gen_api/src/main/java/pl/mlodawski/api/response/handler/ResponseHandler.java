package pl.mlodawski.api.response.handler;

/*
@Author Michał Młodawski
 */
/**
 * Interface defining a contract for handling API responses.
 *
 * This interface is designed for classes that process or handle specific types of responses.
 * Implementations should provide logic to determine if a given response can be handled and
 * process the response accordingly.
 *
 * @param <T> the type of the response being handled
 */
public interface ResponseHandler<T> {

    boolean canHandle(Object response);

    Object handle(Object response);
}

