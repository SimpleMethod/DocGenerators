package pl.mlodawski.docgenerator.pluginsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pl.mlodawski.api.core.PluginContext;
import pl.mlodawski.api.core.PluginOperationRegistry;
import pl.mlodawski.api.response.handler.ResponseHandler;
import pl.mlodawski.docgenerator.pluginsystem.core.context.BasePluginContext;
import pl.mlodawski.docgenerator.pluginsystem.handlers.DefaultResponseHandler;
import pl.mlodawski.docgenerator.pluginsystem.handlers.ResponseHandlerService;

import java.util.List;

/*
@Author Michał Młodawski
*/

/**
 * PluginConfig is a Spring configuration class responsible for defining and managing
 * beans related to the plugin framework and response handling. This configuration
 * facilitates the registration and initialization of core components such as the
 * plugin context, response handling services, and the default response handler.
 *
 * Overview of Beans:
 * - {@code PluginContext}: A central component for managing and executing plugin operations.
 * - {@code ResponseHandlerService}: A service responsible for processing responses using
 *   the registered response handlers.
 * - {@code DefaultResponseHandler}: A fallback handler that processes all responses```java
 /**
 * PluginConfig is a Spring configuration class that defines the necessary beans
 * for the plugin management and response handling system. It provides the setup
 * required for managing plugin operations, handling responses, and establishing
 * default behaviors. By defining these beans, this configuration enables the
 * integration and operation of plugins within the application.
 *
 * Key Beans Provided:
 * - `PluginContext`: The context responsible for managing and executing plugin operations.
 * - `ResponseHandlerService`: The service responsible for coordinating response handlers.
 * - `DefaultResponseHandler`: The default handler in the response processing pipeline.
 */
@Configuration
public class PluginConfig {

    /**
     * Creates a bean for {@link PluginContext}, which is responsible for managing and executing
     * plugin operations using the provided {@link PluginOperationRegistry}.
     *
     * @param operationRegistry the registry containing plugin operations to be executed within the context
     * @return an instance of {@link PluginContext} initialized with the provided operation registry
     */
    @Bean
    public PluginContext pluginContext(PluginOperationRegistry operationRegistry) {
        return new BasePluginContext(operationRegistry);
    }

    /**
     * Creates and provides a {@link ResponseHandlerService} bean configured with a list of response handlers.
     * This service is responsible for coordinating the processing of responses using the supplied handlers.
     *
     * @param handlers a list of {@link ResponseHandler} instances used for processing responses
     * @return an instance of {@link ResponseHandlerService} initialized with the provided response handlers
     */
    @Bean
    public ResponseHandlerService responseHandlerService(List<ResponseHandler<?>> handlers) {
        return new ResponseHandlerService(handlers);
    }

    /**
     * Creates and provides the default response handler bean for managing responses
     * within the application. The {@link DefaultResponseHandler} serves as a fallback
     * handler, ensuring all responses are processed even if no other specialized
     * handler can handle them.
     *
     * @return an instance of {@link DefaultResponseHandler}, which acts as the
     *         default response processor within the application's response handling system.
     */
    @Bean
    public DefaultResponseHandler defaultResponseHandler() {
        return new DefaultResponseHandler();
    }
}