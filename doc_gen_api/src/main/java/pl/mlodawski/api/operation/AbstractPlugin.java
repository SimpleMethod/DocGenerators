package pl.mlodawski.api.operation;

import lombok.extern.slf4j.Slf4j;
import pl.mlodawski.api.core.MiddlewarePlugin;
import pl.mlodawski.api.core.PluginContext;

/*
@Author Michał Młodawski
 */
/**
 * AbstractPlugin serves as a base class for creating plugin implementations
 * that conform to the MiddlewarePlugin interface. It provides default methods
 * for initializing and shutting down a plugin within a given PluginContext.
 */
@Slf4j
public abstract class AbstractPlugin implements MiddlewarePlugin {
    protected PluginContext context;

    /**
     * Initializes the plugin within the provided PluginContext. This method sets
     * the internal context and logs the initialization details, including the plugin's
     * name and version.
     *
     * @param context the context in which the plugin operates, providing access to
     *                logging and operation registry.
     */
    @Override
    public void initialize(PluginContext context) {
        this.context = context;
        context.log(String.format("Initializing plugin: %s version %s", getName(), getVersion()));
    }

    /**
     * Shuts down the plugin and performs any necessary cleanup operations.
     * This method logs the shutdown action using the associated PluginContext.
     * The log includes information about the plugin's name obtained via the
     * {@code getName()} method.
     */
    @Override
    public void shutdown() {
        context.log(String.format("Shutting down plugin: %s", getName()));
    }

}