package pl.mlodawski.docgenerator.pluginsystem.handlers;

import org.springframework.stereotype.Component;

import pl.mlodawski.api.response.handler.ResponseHandler;
import pl.mlodawski.api.response.handler.ResponseProcessor;


/**
 * DefaultResponseHandler is an implementation of the {@link ResponseHandler} interface that acts
 * as a fallback or default handler for processing responses. It is configured to have the lowest
 * priority in the handler processing pipeline through the {@link ResponseProcessor} annotation
 * with an {@code order} value of {@code Integer.MIN_VALUE}.
 *
 * This handler is designed to handle any response object and return it unmodified.
 * It serves as a catch-all when no other handlers in the pipeline can process the given response.
 *
 * Features:
 * - Always returns {@code true} for {@link #canHandle(Object)}, allowing it to handle any response type.
 * - Returns the original response object from the {@link #handle(Object)} method without any modifications.
 * - Integrated as a Spring-managed bean using the {@link Component} annotation.
 *
 * Usage:
 * This handler is typically registered as part of the Spring application context and is used by
 * a service such as {@link ResponseHandlerService} to provide a default response handling
 * mechanism when no other custom handler matches the criteria for processing a specific response.
 */
@Component
@ResponseProcessor(order = Integer.MIN_VALUE)
public class DefaultResponseHandler implements ResponseHandler<Object> {

    @Override
    public boolean canHandle(Object response) {
        return true;
    }

    @Override
    public Object handle(Object response) {

        return response;
    }
}
