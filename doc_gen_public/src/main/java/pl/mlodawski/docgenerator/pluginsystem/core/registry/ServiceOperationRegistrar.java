package pl.mlodawski.docgenerator.pluginsystem.core.registry;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.mlodawski.api.core.PluginOperation;
import pl.mlodawski.api.core.PluginOperationRegistry;

import java.lang.reflect.Method;
import java.util.Map;

/*
@Author Michał Młodawski
*/
/**
 * ServiceOperationRegistrar is responsible for automatically registering service operations
 * found within the application context into the plugin operation registry.
 *
 * It scans Spring beans annotated with {@link org.springframework.stereotype.Service} for methods
 * annotated with {@link ServiceOperation}, extracts operation details, and registers them
 * into a centralized operation registry.
 *
 * The class utilizes Spring's {@link PostConstruct} annotation to trigger the operation registration
 * process during the initialization phase of the application.
 *
 * Components:
 * - It identifies all beans annotated with {@link org.springframework.stereotype.Service}.
 * - It processes public methods in each identified service class and registers those annotated
 *   with {@link ServiceOperation}.
 * - It uses a {@code PluginOperationRegistry} to store and manage registered operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceOperationRegistrar {
    private final ApplicationContext applicationContext;
    private final PluginOperationRegistry operationRegistry;

    /**
     * Registers all service operations within the application context annotated with {@link org.springframework.stereotype.Service}.
     * This method is invoked automatically after the bean's initialization process due to the {@link PostConstruct} annotation.
     * It retrieves all Spring beans annotated with {@link org.springframework.stereotype.Service},
     * scans their methods, and registers methods annotated with {@link ServiceOperation}.
     *
     * For each service bean found, the {@code registerServiceOperations(Object service)} method is called to handle the registration
     * of its annotated methods.
     */
    @PostConstruct
    public void registerOperations() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Service.class);

        for (Object service : beans.values()) {
            registerServiceOperations(service);
        }
    }

    /**
     * Registers service operations annotated with {@link ServiceOperation} within the provided service instance.
     * Scans for all public methods of the given service class. If a method is annotated with
     * {@link ServiceOperation}, extracts the operation identifier from the annotation and registers the method
     * as an operation using the registry system.
     *
     * @param service the service instance containing methods to be registered as operations
     */
    private void registerServiceOperations(Object service) {
        Class<?> serviceClass = service.getClass();

        for (Method method : serviceClass.getMethods()) {
            ServiceOperation annotation = method.getAnnotation(ServiceOperation.class);
            if (annotation != null) {
                String operationId = annotation.value();
                registerOperation(operationId, service, method);
            }
        }
    }

    /**
     * Registers a new operation in the plugin operation registry.
     *
     * @param operationId the unique identifier for the operation
     * @param service the service instance that contains the method to be executed as an operation
     * @param method the method to be registered as the operation
     */
    private void registerOperation(String operationId, Object service, Method method) {
        PluginOperation<?> operation = new DynamicPluginOperation(operationId, service, method);
        operationRegistry.registerOperation(operation);
        log.info("Registered operation {} for method {}.{}",
                operationId, service.getClass().getSimpleName(), method.getName());
    }
}

