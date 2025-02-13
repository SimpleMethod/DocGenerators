package pl.mlodawski.docgenerator.pluginsystem.core.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.mlodawski.api.core.PluginContext;
import pl.mlodawski.api.core.PluginOperation;
import pl.mlodawski.api.core.PluginOperationRegistry;

/*
@Author Michał Młodawski
*/

/**
 * BasePluginContext is an implementation of the {@link PluginContext} interface
 * that provides the foundation for managing plugin operations and logging
 * within the application. This class acts as a central component for performing
 * operations registered in the {@link PluginOperationRegistry} and provides
 * logging functionality to record informational and error messages.
 *
 * Features:
 * - Manages a registry of plugin operations through the {@link PluginOperationRegistry}.
 * - Allows execution of specific plugin operations by their unique identifiers.
 * - Provides logging utilities for tracking execution and errors.
 */
@Slf4j
@RequiredArgsConstructor
public class BasePluginContext implements PluginContext {
    /**
     * A registry for managing and retrieving plugin operations. This variable
     * serves as the core container to hold and provide access to operations
     * supported by the plugin. It is utilized to delegate the execution of
     * specific operations based on the provided operation identifiers.
     */
    private final PluginOperationRegistry operationRegistry;

    /**
     * Executes a specified plugin operation using the given operation identifier and parameters.
     * The operation execution retrieves and invokes an operation from the associated operation registry.
     *
     * @param <T> the expected return type of the operation
     * @param operationId the identifier of the operation to execute
     * @param params the parameters to pass to the operation during its execution
     * @return the result of the executed operation
     * @throws UnsupportedOperationException if no operation associated with the provided identifier is found
     */
    @Override
    public <T> T executeOperation(String operationId, Object... params) {
        log.debug("Executing operation: {} with {} parameters", operationId, params.length);
        PluginOperation<T> operation = operationRegistry.getOperation(operationId);

        if (operation == null) {
            throw new UnsupportedOperationException("Unknown operation: " + operationId);
        }

        return operation.execute(params);
    }

    /**
     * Retrieves the operation registry associated with the plugin context.
     *
     * @return the {@link PluginOperationRegistry} instance used to manage and retrieve plugin operations.
     */
    @Override
    public PluginOperationRegistry getOperationRegistry() {
        return null;
    }

    /**
     * Logs an informational message.
     *
     * @param message the message to be logged
     */
    @Override
    public void log(String message) {
        log.info(message);
    }

    /**
     * Logs an error message and the associated throwable.
     *
     * @param message The error message to log.
     * @param error The throwable associated with the error.
     */
    @Override
    public void logError(String message, Throwable error) {
        log.error(message, error);
    }
}