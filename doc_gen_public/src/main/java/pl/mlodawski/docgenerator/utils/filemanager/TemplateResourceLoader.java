package pl.mlodawski.docgenerator.utils.filemanager;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import pl.mlodawski.docgenerator.utils.filemanager.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

/*
@Author Michał Młodawski
*/
/**
 * A Spring-managed component responsible for handling template resource operations.
 *
 * The {@code TemplateResourceLoader} class processes and manages template resource files
 * by leveraging the {@link TemplateConfiguration} for path resolution. It initializes
 * necessary directory structures and dynamically loads template file contents into
 * annotated fields using the {@code @TemplateResource} annotation. Additionally, it
 * integrates into the Spring lifecycle as a {@code BeanPostProcessor} to handle
 * post-initialization tasks.
 *
 * Key Responsibilities:
 * 1. Ensuring required template directories are created during initialization.
 * 2. Processing fields annotated with {@code @TemplateResource} to load content from
 *    the specified template files.
 * 3. Logging and error handling for template-related operations.
 *
 * This component promotes consistency in the application's management of templates
 * and their associated resources.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateResourceLoader implements BeanPostProcessor {

    /**
     * Represents the configuration for managing template files within the application.
     *
     * The {@code configuration} object is an instance of {@link TemplateConfiguration},
     * which is responsible for determining the base path for template resources and initializing
     * the required directory structure. It is primarily used to assist in locating and loading
     * template files for various operations.
     *
     * This field is immutable and initialized through dependency injection, ensuring
     * that the template configuration remains consistent throughout the application's lifetime.
     */
    private final TemplateConfiguration configuration;

    /**
     * Initializes the component after its dependencies have been injected.
     *
     * This method is annotated with {@code @PostConstruct} and is invoked
     * automatically by the Spring framework during the bean's lifecycle.
     * It ensures the creation and setup of necessary template directories
     * that are required for this component to function properly.
     *
     * The specific logic for creating template directories is delegated to the
     * {@code createTemplateDirectories} method.
     */
    @PostConstruct
    public void init() {
        createTemplateDirectories();
    }

    /**
     * Creates the directories required for template storage.
     *
     * This method initializes the necessary directory structure for storing
     * template resources. It resolves the base path from the {@code TemplateConfiguration}
     * to locate the "module-card" directory and its subdirectory "images". If these directories
     * do not exist, they are created.
     *
     * In case of any IO exception during the directory creation process,
     * a {@code ResourceNotFoundException} is thrown, and an error is logged.
     *
     * This method is typically invoked during the initialization phase of the
     * {@code TemplateResourceLoader} to ensure the template directory structure is available.
     *
     * Exceptions:
     * - Throws {@code ResourceNotFoundException} if directory creation fails.
     */
    private void createTemplateDirectories() {
        try {
            Path moduleCardPath = configuration.getBasePath().resolve("module-card");
            Path imagesPath = moduleCardPath.resolve("images");

            Files.createDirectories(imagesPath);

            log.info("Created template directories at: {}", configuration.getBasePath().toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create template directories", e);
            throw new ResourceNotFoundException("Failed to create template directories", e);
        }
    }

    /**
     * Post-processes a bean after its initialization in the Spring lifecycle.
     *
     * This method inspects the declared fields of the provided bean for the presence
     * of the {@code @TemplateResource} annotation. For each annotated field, it invokes
     * the {@code processTemplateResource} method to handle the field's processing.
     *
     * @param bean The bean instance that has been initialized.
     * @param beanName The name of the bean in the Spring context.
     * @return The processed bean instance, potentially modified to include
     *         initialized values for fields annotated with {@code @TemplateResource}.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TemplateResource.class)) {
                processTemplateResource(bean, field);
            }
        }
        return bean;
    }

    /**
     * Processes a field annotated with {@code @TemplateResource} within a given bean.
     *
     * This method resolves the file path for a template resource specified by the
     * {@code module} and {@code path} attributes of the {@code @TemplateResource} annotation.
     * It reads the file content and assigns it to the annotated field within the bean.
     * If the template resource file is not found or an error occurs during processing,
     * a {@code ResourceNotFoundException} is thrown.
     *
     * @param bean The bean instance which contains the annotated field.
     * @param field The field annotated with {@code @TemplateResource}, which will be processed.
     * @throws ResourceNotFoundException If the template resource file is not found
     *                                   or an error occurs during field assignment.
     */
    private void processTemplateResource(Object bean, Field field) {
        try {
            TemplateResource annotation = field.getAnnotation(TemplateResource.class);
            String modulePath = annotation.module();
            String resourcePath = annotation.path();

            Path fullPath = configuration.getBasePath().resolve(modulePath).resolve(resourcePath);
            File file = fullPath.toFile();

            if (!file.exists()) {
                log.error("Template file not found: {}", fullPath.toAbsolutePath());
                throw new ResourceNotFoundException("Template file not found: " + fullPath.toAbsolutePath());
            }

            field.setAccessible(true);
            field.set(bean, Files.readAllBytes(fullPath));

            log.info("Loaded template resource: {}", fullPath.toAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to process template resource for field: " + field.getName(), e);
            throw new ResourceNotFoundException("Failed to process template resource: " + field.getName(), e);
        }
    }
}