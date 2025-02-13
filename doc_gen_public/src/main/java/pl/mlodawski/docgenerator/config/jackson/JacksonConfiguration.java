package pl.mlodawski.docgenerator.config.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 @Author Michał Młodawski
 */
/**
 * Configuration class for customizing Jackson ObjectMapper settings.
 * This class defines a Spring Bean for ObjectMapper that can be used
 * throughout the application for JSON serialization and deserialization.
 */
@Configuration
public class JacksonConfiguration {
    /**
     * Creates and configures an instance of Jackson's ObjectMapper as a Spring Bean.
     * The returned ObjectMapper is customized to ignore unknown properties during
     * deserialization and to support Java 8 Date and Time API types.
     *
     * @return a configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
