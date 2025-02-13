package pl.mlodawski.docgenerator.pluginsystem.core.registry;

import lombok.RequiredArgsConstructor;
import pl.mlodawski.api.core.PluginOperation;

import java.lang.reflect.Method;

/*
@Author Michał Młodawski
*/
/**
 * Represents a dynamic implementation of the {@link PluginOperation} interface.
 * This class is responsible for wrapping a service method and allowing it to be
 * executed as a plugin operation within the system. It leverages Java reflection
 * to invoke methods dynamically at runtime.
 *
 * This class is typically used in conjunction with the {@link ServiceOperationRegistrar},
 * which identifies and registers service methods annotated with {@link ServiceOperation}
 * as plugin operations.
 *
 * Key Responsibilities:
 * - Stores metadata about the operation, including its unique identifier, the service
 *   instance containing the method, and the method itself.
 * - Provides functionality to retrieve the operation ID and result type.
 * - Executes the wrapped method with provided parameters.
 *
 * Exception Handling:
 * In case of method invocation failure, a {@link RuntimeException} is thrown, wrapping the
 * underlying exception, with details of the operation ID to help with debugging.
 *
 * Immutable:
 * This class is designed to be immutable. The fields are initialized through the constructor
 * and cannot be modified afterward.
 *
 * Threads:
 * Instances of this class are thread-safe, given that they perform no mutable state changes
 * and their operations rely on thread-safe reflection mechanisms.
 */
@RequiredArgsConstructor
public class DynamicPluginOperation implements PluginOperation<Object> {
    private final String operationId;
    private final Object service;
    private final Method method;

    @Override
    public String getOperationId() {
        return operationId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Object> getResultType() {
        return (Class<Object>) method.getReturnType();
    }

    @Override
    public Object execute(Object... params) {
        try {
            return method.invoke(service, params);
        } catch (Exception e) {
            throw new RuntimeException("Error executing operation " + operationId, e);
        }
    }
}

