package pl.mlodawski.docgenerator.pluginsystem.core.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.mlodawski.api.core.PluginOperation;
import pl.mlodawski.api.core.PluginOperationRegistry;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
@Author Michał Młodawski
*/
/**
 * DefaultPluginOperationRegistry serves as the primary registry for managing and accessing
 * plugin operations within the application. It extends the base functionality of the
 * {@link PluginOperationRegistry} class and is marked as a primary Spring component.
 *
 * The class maintains a thread-safe collection of operations and provides mechanisms to
 * register, retrieve, and inspect registered operations. Each registered operation is
 * uniquely identified by an operation ID.
 *
 * Key Features:
 * - Automatically initializes and registers a list of operations provided during construction.
 * - Supports dynamic registration of new operations at runtime.
 * - Provides a method to retrieve operations by their unique identifier, ensuring type safety.
 * - Exposes the set of all available operation IDs for inspection.
 *
 * Thread-Safety:
 * - The use of a {@link ConcurrentHashMap} ensures thread-safe access to the operation registry.
 *
 * Dependencies:
 * - Spring framework is leveraged for dependency injection and component management.
 * - Logging is facilitated using the Lombok {@code @Slf4j} annotation.
 *
 * Constructor:
 * - Accepts a list of {@link PluginOperation} instances, which are automatically registered.
 *   This ensures the registry is pre-populated with operations at application startup.
 *
 * Methods:
 * - {@link #registerOperation(PluginOperation)}: Registers a new plugin operation.
 * - {@link #getOperation(String)}: Retrieves a registered plugin operation by its ID.
 * - {@link #getAvailableOperations()}: Returns the set of all registered operation IDs.
 */
@Primary
@Component
@Slf4j
public class DefaultPluginOperationRegistry extends PluginOperationRegistry {
    /**
     * A thread-safe map that stores registered plugin operations, where each operation
     * is identified by a unique string ID. The map ensures safe concurrent access
     * during operation registration and retrieval.
     *
     * Key characteristics:
     * - Utilizes {@link ConcurrentHashMap} to manage concurrency among multiple threads.
     * - Maps a string identifier to a {@link PluginOperation} instance.
     * - Guarantees type-safety by parameterizing {@link PluginOperation} with a wildcard.
     *
     * Purpose:
     * - Acts as the underlying storage mechanism for plugin operations within the
     *   {@link DefaultPluginOperationRegistry}.
     * - Provides a centralized repository for operations accessible by their unique identifiers.
     *
     * Thread-Safety:
     * - All operations on this map are thread-safe due to the use of {@link ConcurrentHashMap}.
     */
    private final Map<String, PluginOperation<?>> operations = new ConcurrentHashMap<>();

    /**
     * Constructs a DefaultPluginOperationRegistry and automatically registers
     * a list of provided plugin operations. Each plugin operation is identified
     * by its unique operation ID and can be retrieved or managed through this registry.
     *
     * @param pluginOperations the list of plugin operations to be registered at
     *                         initialization. Each operation in the list is registered
     *                         using the operation's unique identifier.
     */
    @Autowired
    public DefaultPluginOperationRegistry(List<PluginOperation<?>> pluginOperations) {
        pluginOperations.forEach(this::registerOperation);
    }

    /**
     * Registers a plugin operation within the operation registry. Each operation is identified
     * by a unique operation ID and associated with a specific result type.
     *
     * If an operation with the same ID already exists in the registry, it will be replaced.
     * This method logs*/
    public void registerOperation(PluginOperation<?> operation) {
        log.info("Registering operation: {} with result type: {}",
                operation.getOperationId(),
                operation.getResultType().getSimpleName());
        operations.put(operation.getOperationId(), operation);
    }

    /**
     * Retrieves a plugin operation associated with the specified operation ID.
     * This method provides type safety by casting the retrieved operation to the expected type.
     *
     * @param <T> The expected return type of the plugin operation.
     * @param operationId The unique identifier of the desired plugin operation.
     *                    It must match the ID of an operation that is already registered.
     * @return The plugin operation corresponding to the specified operation ID,
     *         cast to the expected type. Returns {@code null} if no operation is found
     *         for the given operation ID.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> PluginOperation<T> getOperation(String operationId) {
        return (PluginOperation<T>) operations.get(operationId);
    }

    /**
     * Retrieves the set of all registered operation IDs available in the registry.
     * This set represents the unique identifiers for operations that have been
     * registered and are accessible through the registry.
     *
     * @return a set of strings, where each string is the unique identifier of
     *         a registered plugin operation.
     */
    public Set<String> getAvailableOperations() {
        return operations.keySet();
    }
}
