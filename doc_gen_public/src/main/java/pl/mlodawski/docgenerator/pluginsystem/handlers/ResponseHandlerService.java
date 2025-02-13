package pl.mlodawski.docgenerator.pluginsystem.handlers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import pl.mlodawski.api.response.handler.ResponseHandler;
import pl.mlodawski.api.response.handler.ResponseProcessor;


import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseHandlerService {
    private final List<ResponseHandler<?>> handlers;

    /**
     * Initializes the response handlers by sorting them in descending order
     * based on the value of the {@code order} attribute of the {@link ResponseProcessor} annotation.
     * Handlers without the annotation are assigned a default order of 0.
     * After sorting, the registered response handlers and their respective order values
     * are logged for tracking.
     *
     * This method is automatically invoked after the bean's properties are set
     * but before it is made available for use, as indicated by the {@link PostConstruct} annotation.
     */
    @PostConstruct
    public void init() {
        handlers.sort(Comparator
                .<ResponseHandler<?>>comparingInt(handler -> {
                    ResponseProcessor annotation = handler.getClass().getAnnotation(ResponseProcessor.class);
                    return annotation != null ? annotation.order() : 0;
                })
                .reversed());

        log.info("Registered response handlers:");
        handlers.forEach(handler -> {
            ResponseProcessor annotation = handler.getClass().getAnnotation(ResponseProcessor.class);
            int order = annotation != null ? annotation.order() : 0;
            log.info("  - {} (order: {})", handler.getClass().getSimpleName(), order);
        });
    }

    /**
     * Processes the provided response using the appropriate handler from a list of registered handlers.
     * If no handler is found that can process the response, the original response is returned.
     *
     * @param response the response object to be handled
     * @return the processed response object if a suitable handler is found; otherwise, the original response
     */
    public Object handleResponse(Object response) {
        for (ResponseHandler<?> handler : handlers) {
            if (handler.canHandle(response)) {
                log.debug("Using handler {} for response type {}",
                        handler.getClass().getSimpleName(),
                        response.getClass().getSimpleName());
                return handler.handle(response);
            }
        }
        log.debug("No handler found for response type: {}, returning original response",
                response.getClass().getSimpleName());
        return response;
    }
}
