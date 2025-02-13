package pl.mlodawski.docgenerator.utils.filemanager;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
@Author Michał Młodawski
*/
/**
 * Configuration class for managing the base path of template resources.
 *
 * The {@code TemplateConfiguration} class is a Spring-managed component
 * responsible for initializing and providing the base directory path where
 * template resources are stored. The base path is dynamically initialized
 * during the post-construction phase of the bean lifecycle, ensuring that
 * template files can be consistently located throughout the application.
 *
 * Key Features:
 * 1. Resolves the base path for templates relative to the application's
 *    working directory.
 * 2. Provides the base path for accessing template resources in other components.
 *
 * This configuration integrates seamlessly with template handling components
 * like the {@code TemplateResourceLoader}, where the resolved base path is
 * used to load and manage template resource files.
 */
@Component
@Getter
public class TemplateConfiguration {
    private Path basePath;

    @PostConstruct
    public void init() throws IOException {
        String userDir = System.getProperty("user.dir");
        basePath = Paths.get(userDir, "templates");

        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }
    }
}