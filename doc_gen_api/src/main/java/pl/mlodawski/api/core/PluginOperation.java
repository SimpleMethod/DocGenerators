package pl.mlodawski.api.core;

/*
@Author Michał Młodawski
*/
/**
 * Represents an operation that can be executed within the context of a plugin.
 *
 * This interface defines the contract for operations that plugins can register,
 * retrieve, and execute dynamically. A plugin operation is identified by a unique
 * operation ID and produces a result of a specific type upon execution.
 *
 * @param <T> the type of result that the operation produces upon execution
 */
public interface PluginOperation<T> {
    /**
     * Retrieves the unique identifier of this operation.
     *
     * The operation ID uniquely identifies the plugin operation and is used for
     * registering and retrieving the operation in the plugin operation registry.
     *
     * @return the unique operation identifier as a string
     */
    String getOperationId();
    /**
     * Retrieves the result type of the operation.
     *
     * This method provides the {@code Class} object representing the type of the result
     * that the operation produces when executed.
     *
     * @return the {@code Class} object representing the result type of the operation
     */
    Class<T> getResultType();
    /**
     * Executes the plugin operation with the provided parameters.
     *
     * The implementation of this method defines how the operation processes the given parameters
     * and produces the result. The specific behavior is determined by the concrete implementation
     * of the operation.
     *
     * @param params the parameters required for executing the operation
     * @return the result of the operation execution
     */
    T execute(Object... params);
}

