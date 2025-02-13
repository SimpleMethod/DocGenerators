package pl.mlodawski.api.core;

/*
@Author Michał Młodawski
 */
/**
 * Represents the contract for middleware plugins within the plugin framework.
 *
 * MiddlewarePlugin provides a standardized interface for defining and managing plugins
 * that can be initialized, shut down, and queried for metadata such as their unique identifier,
 * name, and version. Implementations should adhere to this interface to ensure compatibility
 * with the middleware plugin lifecycle and context.
 */
public interface MiddlewarePlugin {
    /**
     * Retrieves the unique identifier of the middleware plugin.
     *
     * The identifier is used to uniquely distinguish this plugin
     * within the context of a plugin management system.
     *
     * @return the unique identifier of the plugin as a string
     */
    String getId();
    /**
     * Retrieves the name of the plugin.
     *
     * The plugin name is a human-readable identifier that provides a descriptive representation
     * of the plugin within the system. It is used for logging, display, and debugging purposes.
     *
     * @return the name of the plugin as a string
     */
    String getName();
    /**
     * Retrieves the version of the plugin.
     *
     * This method returns the version string representing the current version
     * of the plugin. The version information is typically used for informational
     * or compatibility purposes.
     *
     * @return the version of the plugin as a String
     */
    String getVersion();
    /**
     * Initializes the middleware plugin within the given plugin context.
     *
     * This method is called to prepare the plugin for operation by performing
     * necessary setup configurations, resource initializations, or
     * context-specific bindings required for the plugin's lifecycle.
     *
     * @param context the plugin context within which the middleware plugin operates
     */
    void initialize(PluginContext context);
    /**
     * Shuts down the plugin and releases associated resources or performs cleanup operations.
     *
     * This method is invoked as part of the plugin's lifecycle management to ensure
     * proper termination of its operations. Implementations may include tasks such as
     * closing connections, freeing allocated memory, or stopping background processes.
     */
    void shutdown();
}
