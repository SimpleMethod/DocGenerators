package pl.mlodawski.api.core;

/*
@Author Michał Młodawski
*/

/**
 * Represents the various states of a plugin during its lifecycle.
 *
 * The possible statuses are:
 * - DISCOVERED: The plugin has been identified but not yet initialized.
 * - INITIALIZED: The plugin has been successfully initialized and is ready for further actions.
 * - ENABLED: The plugin is active and operational.
 * - DISABLED: The plugin is inactive and not performing operations.
 * - ERROR: The plugin has encountered an issue and cannot operate correctly.
 */
public enum PluginStatus {
    DISCOVERED,
    INITIALIZED,
    ENABLED,
    DISABLED,
    ERROR
}
