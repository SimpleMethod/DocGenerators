package pl.mlodawski.docgenerator.pluginsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.mlodawski.api.core.MiddlewarePlugin;
import pl.mlodawski.api.core.PluginStatus;


/*
@Author Michał Młodawski
*/
/**
 * Class representing information about a plugin, including its associated plugin instance
 * and its current status.
 */
@Getter
@AllArgsConstructor
public class PluginInfo {
    private final MiddlewarePlugin plugin;
    @Setter
    private PluginStatus status;
}
