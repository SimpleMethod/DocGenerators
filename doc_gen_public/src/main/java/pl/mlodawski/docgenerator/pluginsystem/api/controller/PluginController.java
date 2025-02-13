package pl.mlodawski.docgenerator.pluginsystem.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mlodawski.api.core.MiddlewarePlugin;
import pl.mlodawski.api.core.PluginStatus;
import pl.mlodawski.docgenerator.core.response.BaseApiResponse;
import pl.mlodawski.docgenerator.pluginsystem.core.manager.PluginManager;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 @Author Michał Młodawski
 */
/**
 * The PluginController class provides RESTful APIs for managing plugins and their operations.
 * It allows retrieving plugin information, initializing, enabling, disabling plugins, and invoking
 * specific plugin methods.
 */
@RestController
@RequestMapping("/api/v1/plugins")
@RequiredArgsConstructor
@Slf4j
public class PluginController {
    private final PluginManager pluginManager;

    /**
     * Retrieves a list of available plugins with their details.
     *
     * @return a list of maps where each map represents a plugin with its properties:
     *         "id" (String) - the identifier of the plugin,
     *         "name" (String) - the name of the plugin or "Unknown" if unavailable,
     *         "version" (String) - the version of the plugin or "Unknown" if unavailable,
     *         "status" (String) - the status of the plugin,
     *         "methods" (List<String>) - the supported methods of the plugin.
     */
    @GetMapping
    public List<Map<String, Object>> getAvailablePlugins() {
        return pluginManager.getAvailablePlugins().stream()
                .map(pluginId -> {
                    MiddlewarePlugin plugin = pluginManager.getPlugin(pluginId);
                    return Map.of(
                            "id", pluginId,
                            "name", plugin != null ? plugin.getName() : "Unknown",
                            "version", plugin != null ? plugin.getVersion() : "Unknown",
                            "status", pluginManager.getPluginStatus(pluginId),
                            "methods", pluginManager.getPluginMethods(pluginId)
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Initializes a plugin with the given plugin identifier.
     *
     * @param pluginId the unique identifier of the plugin to be initialized
     * @return a ResponseEntity containing a success message if the plugin is initialized successfully
     *         or an error message with a status of INTERNAL_SERVER_ERROR if an exception occurs
     */
    @PostMapping("/{pluginId}/initialize")
    public ResponseEntity<String> initializePlugin(@PathVariable String pluginId) {
        try {
            pluginManager.initializePlugin(pluginId);
            return ResponseEntity.ok("Plugin initialized");
        } catch (Exception e) {
            log.error("Error initializing plugin {}: {}", pluginId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initialize plugin: " + e.getMessage());
        }
    }

    /**
     * Enables a plugin specified by its unique plugin identifier.
     *
     * @param pluginId the unique identifier of the plugin to be enabled
     * @return a ResponseEntity containing a success message if the plugin is successfully enabled
     *         or a bad request response with an error message if the plugin could not be enabled
     */
    @PostMapping("/{pluginId}/enable")
    public ResponseEntity<String> enablePlugin(@PathVariable String pluginId) {
        boolean success = pluginManager.enablePlugin(pluginId);
        if (success) {
            return ResponseEntity.ok("Plugin enabled");
        } else {
            return ResponseEntity.badRequest().body("Failed to enable plugin");
        }
    }

    /**
     * Disables the plugin specified by the given plugin ID.
     *
     * @param pluginId the ID of the plugin to be disabled
     * @return a ResponseEntity containing a success message if the plugin was disabled successfully
     *         or an error message if the operation failed
     */
    @PostMapping("/{pluginId}/disable")
    public ResponseEntity<String> disablePlugin(@PathVariable String pluginId) {
        boolean success = pluginManager.disablePlugin(pluginId);
        if (success) {
            return ResponseEntity.ok("Plugin disabled");
        } else {
            return ResponseEntity.badRequest().body("Failed to disable plugin");
        }
    }

    /**
     * Retrieves the status of a plugin by its identifier.
     *
     * @param pluginId the unique identifier of the plugin
     * @return a ResponseEntity containing the PluginStatus if found, or a ResponseEntity with a not found status if the plugin does not exist
     */
    @GetMapping("/{pluginId}/status")
    public ResponseEntity<PluginStatus> getPluginStatus(@PathVariable String pluginId) {
        PluginStatus status = pluginManager.getPluginStatus(pluginId);
        if (status != null) {
            return ResponseEntity.ok(status);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves a list of method names available for a specific plugin.
     *
     * @param pluginId the unique identifier of the plugin
     * @return a ResponseEntity containing a list of method names available in the specified plugin
     */
    @GetMapping("/{pluginId}/methods")
    public ResponseEntity<List<String>> getPluginMethods(@PathVariable String pluginId) {
        List<String> methods = pluginManager.getPluginMethods(pluginId);
        return ResponseEntity.ok(methods);
    }

    /**
     * Invokes a specified method of a plugin by its identifier with provided parameters.
     *
     * @param pluginId   the unique identifier of the plugin to invoke the method on
     * @param methodName the name of the method to invoke
     * @param queryParams a map of query parameters to pass to the plugin method, can be null
     * @param bodyParams  a map of body parameters to pass to the plugin method, can be null
     * @return a ResponseEntity containing the result of the invoked plugin method wrapped in a BaseApiResponse
     * @throws Exception if an error occurs during the invocation of the plugin method
     */
    @PostMapping("/{pluginId}/invoke/{methodName}")
    public ResponseEntity<?> invokePluginMethod(
            @PathVariable String pluginId,
            @PathVariable String methodName,
            @RequestParam(required = false) Map<String, String> queryParams,
            @RequestBody(required = false) Map<String, Object> bodyParams) throws Exception {
        try {
            Map<String, Object> allParams = new LinkedHashMap<>();
            if (queryParams != null) {
                allParams.putAll(queryParams);
            }
            if (bodyParams != null) {
                allParams.putAll(bodyParams);
            }

            Class<?> returnType = pluginManager.getMethodReturnType(pluginId, methodName, allParams);

            Object result = pluginManager.invokePluginMethod(pluginId, methodName, allParams);


            return ResponseEntity.ok(BaseApiResponse.success(result));

        } catch (RuntimeException e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause instanceof Exception) {
                throw (Exception) cause;
            } else {
                throw new Exception("Unknown error occurred", cause);
            }
        }
    }


    /**
     * Retrieves the list of method signatures for the specified plugin.
     *
     * @param pluginId the unique identifier of the plugin for which to fetch method signatures
     * @return a ResponseEntity containing a list of method signatures if found, or a not found response if no signatures are available
     */
    @GetMapping("/{pluginId}/methods/signatures")
    public ResponseEntity<List<String>> getMethodSignatures(@PathVariable String pluginId) {
        List<String> signatures = pluginManager.getMethodSignatures(pluginId);
        if (signatures.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(signatures);
    }
}
