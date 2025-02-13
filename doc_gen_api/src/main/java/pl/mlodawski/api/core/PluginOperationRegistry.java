package pl.mlodawski.api.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
@Author Michał Młodawski
*/

/**
 * A registry responsible for managing and retrieving plugin operations.
 *
 * This class provides functionalities for registering, accessing, and checking the existence of
 * plugin operations, enabling the dynamic execution of plugin-based logic.
 */
public class PluginOperationRegistry {
    private final Map<String, PluginOperation<?>> operations = new ConcurrentHashMap<>();

    public void registerOperation(PluginOperation<?> operation) {
        operations.put(operation.getOperationId(), operation);
    }

    @SuppressWarnings("unchecked")
    public <T> PluginOperation<T> getOperation(String operationId) {
        return (PluginOperation<T>) operations.get(operationId);
    }

    public boolean hasOperation(String operationId) {
        return operations.containsKey(operationId);
    }
}