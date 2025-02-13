package pl.mlodawski.docgenerator.pluginsystem.core.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mlodawski.api.core.MiddlewarePlugin;
import pl.mlodawski.api.core.PluginContext;
import pl.mlodawski.api.core.PluginStatus;
import pl.mlodawski.docgenerator.pluginsystem.handlers.ResponseHandlerService;
import pl.mlodawski.docgenerator.pluginsystem.model.PluginInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
@Author Michał Młodawski
 */
/**
 * Manages the lifecycle of plugins, including loading, unloading, monitoring,
 * and maintaining the state of registered plugins.
 *
 * The PluginManager performs essential operations such as creating and monitoring
 * the plugins directory, dynamically loading plugins into the application context,
 * and responding to plugin lifecycle events. It relies on the {@link PluginContext}
 * for maintaining plugin-specific contexts and services like the
 * {@code ResponseHandlerService} for handling interactions with plugins.
 *
 * Plugins are expected to follow the conventions and standards defined,
 * such as placing JAR files in the plugins directory and including proper service
 * definition files for discovery.
 *
 * This class supports features like:
 * - Dynamic loading of plugins from JAR files.
 * - Monitoring for changes in the plugins directory.
 * - Management of plugins' lifecycle (e.g., loading, unloading).
 */
@Service
@Slf4j
public class PluginManager {
    private final PluginContext pluginContext;
    private final Map<String, PluginInfo> plugins = new ConcurrentHashMap<>();
    private final ResponseHandlerService responseHandlerService;
    private final String pluginsDirectory;
    private WatchService watchService;

    public PluginManager(PluginContext pluginContext, ResponseHandlerService responseHandlerService) {
        this.pluginContext = pluginContext;
        this.responseHandlerService = responseHandlerService;

        String currentDirectory = System.getProperty("user.dir");
        this.pluginsDirectory = Paths.get(currentDirectory, "plugins").toString();


        createPluginsDirectoryIfNotExists();

        loadExistingPlugins();
        startFileWatcher();
    }
    /**
     * Ensures that the plugins directory exists.
     * If the directory does not exist, it attempts to create it.
     * Logs an informational message upon successful creation of the directory
     * or an error message if the directory creation fails*/
    private void createPluginsDirectoryIfNotExists() {
        File pluginsDir = new File(pluginsDirectory);
        if (!pluginsDir.exists()) {
            if (pluginsDir.mkdir()) {
                log.info("Utworzono katalog plugins: {}", pluginsDirectory);
            } else {
                log.error("Nie udało się utworzyć katalogu plugins: {}", pluginsDirectory);
            }
        }
    }



    /**
     * Loads existing plugins from the specified plugins directory.
     *
     * This method checks for the existence and validity of the plugins directory.
     * If the directory exists and contains JAR files, it iterates over each JAR file
     * and invokes the {@code loadPlugin(File)} method to load the plugin.
     *
     * Logging is performed to provide information about the directory status,
     * JAR files found, and loading processes.
     *
     * If the plugins directory does not exist, is not a directory, or contains no JAR files,
     * appropriate warnings are logged, and the method terminates early.
     */
    private void loadExistingPlugins() {
        File pluginsDir = new File(pluginsDirectory);
        log.info("Checking plugins directory: {}", pluginsDir.getAbsolutePath());

        if (!pluginsDir.exists()) {
            log.warn("Plugins directory does not exist: {}", pluginsDir.getAbsolutePath());
            return;
        }

        if (!pluginsDir.isDirectory()) {
            log.warn("Plugins path is not a directory: {}", pluginsDir.getAbsolutePath());
            return;
        }

        File[] jarFiles = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            log.warn("No JAR files found in plugins directory");
            return;
        }

        log.info("Found {} JAR files in plugins directory", jarFiles.length);
        for (File jar : jarFiles) {
            log.info("Loading plugin from JAR: {}", jar.getAbsolutePath());
            loadPlugin(jar);
        }
    }

    /**
     * Starts a file watcher service that monitors a specified plugins directory for changes.
     * It observes the directory for plugin JAR files being added or removed.
     *
     * The method sets up a watch service to listen for file creation and deletion events in the
     * plugins directory. When a JAR file is added, it attempts to load the plugin. When a JAR file
     * is removed, it attempts to unload the associated plugin.
     *
     * The file watcher runs in its own background thread, continuously polling events from the
     * watch service. If the service fails to continue watching the directory, the thread will exit.
     *
     * If any I/O errors occur while initializing the file watch service, an error is logged.
     *
     * This method is designed for plugins management in a directory where the structure is assumed
     * to follow expected conventions, such as plugin JAR files being placed in the directory.
     */
    private void startFileWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(pluginsDirectory);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            new Thread(() -> {
                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path fileName = (Path) event.context();

                        if (kind == StandardWatchEventKinds.ENTRY_CREATE && fileName.toString().endsWith(".jar")) {
                            loadPlugin(new File(pluginsDirectory, fileName.toString()));
                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            String pluginId = fileName.toString().replace(".jar", "");
                            unloadPlugin(pluginId);
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            log.error("Error starting file watcher", e);
        }
    }

    /**
     * Loads a plugin from the specified JAR file by creating a class loader, discovering
     * plugin implementations through the ServiceLoader mechanism, and registering the plugins.
     *
     * This method checks for the presence of a service definition file in the JAR and
     * attempts to load plugin classes defined within it. If a plugin is successfully loaded,
     * it is added to the plugin registry.
     *
     * @param jar The JAR file from which the plugin should be loaded. This must be a valid
     *            file containing a service definition for {@code MiddlewarePlugin} under
     *            {@code META-INF/services/}.
     */
    private void loadPlugin(File jar) {
        String jarName = jar.getName().replace(".jar", "");
        URLClassLoader classLoader = null;

        try {
            log.info("Creating URLClassLoader for JAR: {}", jar.getAbsolutePath());
            classLoader = new URLClassLoader(
                    new URL[]{jar.toURI().toURL()},
                    this.getClass().getClassLoader()
            );

            URL serviceFile = classLoader.getResource("META-INF/services/pl.mlodawski.api.core.MiddlewarePlugin");
            if (serviceFile == null) {
                log.error("Service file not found in JAR: {}", jarName);
                return;
            }
            log.info("Found service file: {}", serviceFile);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(serviceFile.openStream()))) {
                String line;
                log.info("Service file content for {}: ", jarName);
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("#")) {
                        log.info("Service class defined: {}", line.trim());
                    }
                }
            }

            log.info("Loading plugins using ServiceLoader for JAR: {}", jarName);
            ServiceLoader<MiddlewarePlugin> loader = ServiceLoader.load(MiddlewarePlugin.class, classLoader);
            Iterator<MiddlewarePlugin> iterator = loader.iterator();

            boolean found = false;
            while (iterator.hasNext()) {
                try {
                    MiddlewarePlugin plugin = iterator.next();
                    found = true;
                    String pluginId = plugin.getId();
                    log.info("Found plugin: {} ({})", plugin.getName(), pluginId);

                    PluginInfo pluginInfo = new PluginInfo(plugin, PluginStatus.DISCOVERED);
                    plugins.put(pluginId, pluginInfo);

                    log.info("Successfully loaded plugin: {} ({}) from JAR: {}",
                            plugin.getName(), pluginId, jarName);
                } catch (ServiceConfigurationError e) {
                    log.error("Error loading plugin implementation: {}", e.getMessage(), e);
                }
            }

            if (!found) {
                log.warn("No plugins found in JAR: {}", jarName);
            }
        } catch (Exception e) {
            log.error("Error loading plugin from {}: {}", jar.getAbsolutePath(), e.getMessage(), e);
            plugins.put(jarName, new PluginInfo(null, PluginStatus.ERROR));
        } finally {
            if (classLoader != null) {
                try {
                    classLoader.close();
                } catch (IOException e) {
                    log.error("Error closing classloader: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Unloads the specified plugin by its identifier. This method removes the plugin from
     * the internal plugins registry and attempts to shut it down if it was loaded successfully.
     *
     * @param pluginId the unique identifier of the plugin to be unloaded
     */
    private void unloadPlugin(String pluginId) {
        PluginInfo pluginInfo = plugins.remove(pluginId);
        if (pluginInfo != null && pluginInfo.getPlugin() != null) {
            try {
                pluginInfo.getPlugin().shutdown();
                log.info("Unloaded plugin: {}", pluginId);
            } catch (Exception e) {
                log.error("Error unloading plugin {}: {}", pluginId, e.getMessage(), e);
            }
        }
    }

    /**
     * Initializes the specified plugin by its unique identifier. This method retrieves the
     * plugin information, checks its current status, and initializes it if it is in the
     * DISCOVERED state. If initialization is successful, the plugin's status is updated to
     * INITIALIZED. If an error occurs during initialization, the status is updated to ERROR*/
    public void initializePlugin(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo != null && pluginInfo.getStatus() == PluginStatus.DISCOVERED) {
            try {
                pluginInfo.getPlugin().initialize(pluginContext);
                pluginInfo.setStatus(PluginStatus.INITIALIZED);
                log.info("Initialized plugin: {}", pluginId);
            } catch (Exception e) {
                log.error("Error initializing plugin {}: {}", pluginId, e.getMessage(), e);
                pluginInfo.setStatus(PluginStatus.ERROR);
            }
        }
    }

    /**
     * Enables the specified plugin by its ID if its current status is either
     * {@code INITIALIZED} or {@code DISABLED}. Updates the plugin's status to
     * {@code ENABLED} and logs the status change. If an error occurs during
     * the enabling process, sets the status to {@code ERROR} and logs the error.
     *
     * @param pluginId the unique identifier of the plugin to be enabled
     * @return {@code true} if the plugin was successfully enabled, {@code false}
     *         otherwise
     */
    public boolean enablePlugin(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo != null &&
                (pluginInfo.getStatus() == PluginStatus.INITIALIZED ||
                        pluginInfo.getStatus() == PluginStatus.DISABLED)) {
            try {
                pluginInfo.setStatus(PluginStatus.ENABLED);
                log.info("Enabled plugin: {}", pluginId);
                return true;
            } catch (Exception e) {
                log.error("Error enabling plugin {}: {}", pluginId, e.getMessage(), e);
                pluginInfo.setStatus(PluginStatus.ERROR);
                return false;
            }
        }
        return false;
    }

    /**
     * Disables a plugin identified by its unique ID if it is currently enabled.
     *
     * @param pluginId the unique identifier of the plugin to disable
     * @return true if the plugin was successfully disabled, false otherwise
     */
    public boolean disablePlugin(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo != null && pluginInfo.getStatus() == PluginStatus.ENABLED) {
            try {
                pluginInfo.setStatus(PluginStatus.DISABLED);
                log.info("Disabled plugin: {}", pluginId);
                return true;
            } catch (Exception e) {
                log.error("Error disabling plugin {}: {}", pluginId, e.getMessage(), e);
                pluginInfo.setStatus(PluginStatus.ERROR);
                return false;
            }
        }
        return false;
    }

    /**
     * Retrieves a list of available plugin names.
     *
     * @return a list of strings representing the names of all available plugins
     */
    public List<String> getAvailablePlugins() {
        return new ArrayList<>(plugins.keySet());
    }

    /**
     * Retrieves the status of a plugin based on the provided plugin identifier.
     *
     * @param pluginId the unique identifier of the plugin whose status is to be retrieved
     * @return the status of the specified plugin as a PluginStatus object, or null if the plugin does not exist
     */
    public PluginStatus getPluginStatus(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        return pluginInfo != null ? pluginInfo.getStatus() : null;
    }

    /**
     * Retrieves a list of method signatures for a specified plugin based on its identifier.
     *
     * @param pluginId the unique identifier of the plugin for which to retrieve method signatures
     * @return a list of strings representing the method signatures, including return types,
     *         method names, and parameters; returns an empty list if the plugin is not found
     *         or no methods are available
     */
    public List<String> getPluginMethods(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo != null && pluginInfo.getPlugin() != null) {
            List<String> methodDescriptions = new ArrayList<>();
            for (Method method : pluginInfo.getPlugin().getClass().getDeclaredMethods()) {
                if (method.getDeclaringClass().equals(MiddlewarePlugin.class)) {
                    continue;
                }

                StringBuilder methodDesc = new StringBuilder();
                methodDesc.append(method.getReturnType().getSimpleName())
                        .append(" ")
                        .append(method.getName())
                        .append("(");

                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (i > 0) {
                        methodDesc.append(", ");
                    }
                    methodDesc.append(param.getType().getSimpleName())
                            .append(" ")
                            .append(param.getName());
                }
                methodDesc.append(")");

                methodDescriptions.add(methodDesc.toString());
            }
            return methodDescriptions;
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves the middleware plugin associated with the specified plugin identifier.
     *
     * @param pluginId the identifier of the plugin to retrieve
     * @return the MiddlewarePlugin instance if the plugin exists, or null if no plugin is associated with the given identifier
     */
    public MiddlewarePlugin getPlugin(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        return pluginInfo != null ? pluginInfo.getPlugin() : null;
    }

    /**
     * Invokes a specified method on a plugin identified by its plugin ID.
     * This method attempts to find and execute a method on the plugin instance.
     * It ensures that the plugin is enabled before invoking the method.
     *
     * @param pluginId the unique identifier of the plugin
     * @param methodName the name of the method to be invoked on the plugin
     * @param params a map containing the parameters to be passed to the method
     * @return the result of the method invocation, or null if the method does not return a value
     * @throws IllegalArgumentException if the plugin with the specified pluginId is not found
     * @throws IllegalStateException if the plugin with the specified pluginId is not enabled
     * @throws RuntimeException if any error occurs during method invocation
     */
    public Object invokePluginMethod(String pluginId, String methodName, Map<String, Object> params) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo == null || pluginInfo.getPlugin() == null) {
            throw new IllegalArgumentException("Plugin not found: " + pluginId);
        }

        if (pluginInfo.getStatus() != PluginStatus.ENABLED) {
            throw new IllegalStateException("Plugin is not enabled: " + pluginId);
        }

        try {
            Method method = findMethod(pluginInfo.getPlugin().getClass(), methodName, params);
            if (method == null) {
                throw new NoSuchMethodException("Method not found: " + methodName);
            }

            Object[] paramValues = prepareParameters(method, params);
            method.setAccessible(true);
            Object result = method.invoke(pluginInfo.getPlugin(), paramValues);


            return result;
        } catch (Exception e) {
            log.error("Error invoking method {} on plugin {}: {}",
                    methodName, pluginId, e.getMessage(), e);
            throw new RuntimeException("Error invoking plugin method", e);
        }
    }

    /**
     * Retrieves the return type of a specified method within a plugin.
     *
     * @param pluginId the unique identifier of the plugin
     * @param methodName the name of the method whose return type is to be determined
     * @param params a map of parameter names and values to identify the method signature
     * @return the return type of the specified method as a {@code Class<?>} object
     * @throws IllegalArgumentException if the plugin or method cannot be found, or if the parameters do not match the method signature
     */
    public Class<?> getMethodReturnType(String pluginId, String methodName, Map<String, Object> params) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo == null || pluginInfo.getPlugin() == null) {
            throw new IllegalArgumentException("Plugin not found: " + pluginId);
        }

        Method method = findMethod(pluginInfo.getPlugin().getClass(), methodName, params);
        if (method == null) {
            throw new IllegalArgumentException(String.format(
                    "Method %s not found in plugin %s with parameters %s",
                    methodName,
                    pluginId,
                    params
            ));
        }

        return method.getReturnType();
    }

    /**
     * Attempts to find a method within a given class that matches the specified method name and parameters.
     *
     * @param clazz the class in which to search for the method
     * @param methodName the name of the method to find
     * @param params a map of parameter names and their corresponding values, used to match the method signature
     * @return the matching Method object if found
     * @throws IllegalArgumentException if no matching method is found in the specified class
     */
    private Method findMethod(Class<?> clazz, String methodName, Map<String, Object> params) {
        Method[] methods = clazz.getMethods();

        log.debug("Looking for method {} with parameters: {}", methodName, params);

        for (Method method : methods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }

            Parameter[] parameters = method.getParameters();

            boolean allParametersPresent = true;
            for (Parameter param : parameters) {
                if (!params.containsKey(param.getName())) {
                    allParametersPresent = false;
                    break;
                }
            }
            if (params.size() != parameters.length) {
                continue;
            }

            if (allParametersPresent) {
                try {
                    Object[] paramValues = prepareParameters(method, new HashMap<>(params));
                    return method;
                } catch (Exception e) {
                    log.debug("Could not match parameters for method {}: {}",
                            method.getName(), e.getMessage());
                }
            }
        }

        StringBuilder errorMessage = new StringBuilder("No matching method found. Available methods:\n");
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                errorMessage.append("  ").append(method.getReturnType().getSimpleName())
                        .append(" ").append(method.getName()).append("(");
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];
                    if (i > 0) {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(param.getType().getSimpleName())
                            .append(" ").append(param.getName());
                }
                errorMessage.append(")\n");
            }
        }

        throw new IllegalArgumentException(errorMessage.toString());
    }

    /**
     * Prepares and maps the provided parameters to the appropriate values
     * based on the method's parameter definition.
     *
     * @param method The target method whose parameters will be mapped.
     * @param params A map containing parameter names as keys and their corresponding
     *               values to be passed to the method.
     * @return An array of objects representing the ordered parameter values prepared
     *         for the method invocation.
     * @throws IllegalArgumentException If a required parameter is missing or if the
     *                                  provided value cannot be converted to the required type.
     */
    private Object[] prepareParameters(Method method, Map<String, Object> params) {
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String paramName = parameter.getName();
            Class<?> paramType = parameter.getType();

            if (!params.containsKey(paramName)) {
                throw new IllegalArgumentException(
                        String.format("Missing required parameter: %s of type %s",
                                paramName, paramType.getSimpleName()));
            }

            Object value = params.get(paramName);
            try {
                paramValues[i] = convertValue(value, paramType);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Invalid value for parameter %s: expected %s, got %s",
                                paramName, paramType.getSimpleName(), value.getClass().getSimpleName()));
            }
        }

        return paramValues;
    }

    /**
     * Converts the given value to the specified target type, if applicable.
     *
     * @param value the value to be converted, typically a {@code String}, but can be any object
     * @param targetType the target type to which the value should be converted
     * @return the converted value if the conversion is supported, or the original value if no conversion is possible
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(strValue);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(strValue);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(strValue);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(strValue);
            }
        }
        return value;
    }

    /**
     * Retrieves the method signatures of all declared methods in the plugin
     * associated with the given plugin ID.
     *
     * @param pluginId the unique identifier of the plugin whose method signatures are to be retrieved
     * @return a list of method signatures in the format of method name and parameter types,
     *         or an empty list if the plugin is not found or invalid
     */
    public List<String> getMethodSignatures(String pluginId) {
        PluginInfo pluginInfo = plugins.get(pluginId);
        if (pluginInfo == null || pluginInfo.getPlugin() == null) {
            return Collections.emptyList();
        }

        List<String> signatures = new ArrayList<>();
        for (Method method : pluginInfo.getPlugin().getClass().getDeclaredMethods()) {
            StringBuilder signature = new StringBuilder();
            signature.append(method.getName()).append("(");

            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) signature.append(", ");
                signature.append(parameters[i].getType().getSimpleName())
                        .append(" ")
                        .append(parameters[i].getName());
            }
            signature.append(")");

            signatures.add(signature.toString());
        }
        return signatures;
    }
}
