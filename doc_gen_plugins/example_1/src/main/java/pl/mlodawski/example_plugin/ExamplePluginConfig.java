package pl.mlodawski.example_plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.mlodawski.api.core.MiddlewarePlugin;

@Configuration
public class ExamplePluginConfig {

    @Bean
    public MiddlewarePlugin examplePlugin() {
        return new ExamplePlugin();
    }
}
