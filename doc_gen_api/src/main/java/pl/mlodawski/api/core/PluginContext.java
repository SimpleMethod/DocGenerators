package pl.mlodawski.api.core;

/*
@Author Michał Młodawski
*/

/**
 * Represents the runtime context in which a plugin operates.
 *
 * This interface provides essential methods for interacting with the plugin framework,
 * including executing plugin-specific operations, accessing the operation registry,
 * and logging messages or errors. It serves as the central point for managing and
 * invoking operations defined by plugins, enabling modular and extensible functionality.
 */
public interface PluginContext {

    /**
     * Executes a plugin operation identified by the given operation ID with the specified parameters.
     *
     * This method retrieves the operation associated with the given operation ID from the
     * operation registry and invokes its execution with the provided parameters. If the operation
     * is not found in the registry, an {@code UnsupportedOperationException} is thrown.
     *
     * @param <T> the type of the result produced by the operation
     * @param operationId the unique identifier of the operation to be executed
     * @param params the parameters to be passed to the operation for execution
     * @return the result of executing the operation
     * @throws UnsupportedOperationException if the operation is not found in the registry
     */
    @SuppressWarnings("unchecked")
    default <T> T executeOperation(String operationId, Object... params) {
        PluginOperation<T> operation = getOperationRegistry().getOperation(operationId);
        if (operation == null) {
            throw new UnsupportedOperationException("Unknown operation: " + operationId);
        }
        return operation.execute(params);
    }


    /**
     * Retrieves the plugin operation registry associated with this context.
     *
     * The operation registry is responsible for managing and providing access to
     * registered plugin operations. It enables dynamic retrieval and invocation
     * of plugin-defined operations.
     *
     * @return the instance of {@code PluginOperationRegistry} used for managing plugin operations
     */
    PluginOperationRegistry getOperationRegistry();


    /**
     * Logs a message within the plugin's context.
     *
     * This method allows plugins to record informational messages that can
     * assist with debugging, tracing, or monitoring plugin behavior during
     * its lifecycle.
     *
     * @param message the message to be logged
     */
    void log(String message);
    /**
     * Logs an error message and the associated exception.
     *
     * This method is used to record error details, including a descriptive message
     * and the exception that caused the error, for debugging or tracking purposes.
     *
     * @param message a descriptive error message
     * @param error   the exception associated with the error
     */
    void logError(String message, Throwable error);
}

