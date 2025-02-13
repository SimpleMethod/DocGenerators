package pl.mlodawski.docgenerator.config.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.boot.logging.LogLevel;
import pl.mlodawski.docgenerator.config.base.BaseConfig;

import java.util.List;

/*
 @Author Michał Młodawski
 */
/**
 * AppConfig is a configuration class that extends {@link BaseConfig} and provides application-specific settings.
 * It encapsulates properties that define the application's operational context, such as timezone, locale, log level,
 * and frontend URL. Validation constraints are applied to ensure the properties contain valid and meaningful values.
 *
 * Fields:
 * - `timezone`: Defines the application's timezone. This field must adhere to a valid timezone format using only
 *   alphabetic characters and underscores.
 * - `locale`: Specifies the locale for the application's internationalization and localization purposes. The locale
 *   must conform to a two-letter language code optionally followed by an underscore and a two-letter country code.
 * - `logLevel`: Represents the application's logging verbosity. This field must not be null, ensuring logging behavior
 *   is always defined.
 * - `frontendUrl`: Indicates the URL for the application's frontend interface. The URL must use either the HTTP or HTTPS
 *   protocol.
 *
 * This class uses annotations to enforce validation rules and ensure the correctness of configuration values:
 * - `@NotBlank`: Ensures that string fields are not empty or blank.
 * - `@Pattern`: Validates the format of string fields using regular expressions.
 * - `@NotNull`: Verifies that the field is not null.
 *
 * This class is equipped with Lombok annotations:
 * - `@EqualsAndHashCode(callSuper = true)`: Ensures proper equality and hash code implementation, including the superclass fields.
 * - `@Data`: Generates getters, setters, toString, hashCode, and equals methods.
 * - `@Builder`: Allows for object construction using the builder pattern.
 * - `@NoArgsConstructor`: Generates a no-argument constructor.
 * - `@AllArgsConstructor`: Generates a constructor with all fields as arguments.
 *
 * The AppConfig class is typically used to encapsulate application-wide settings and is a key component
 * for maintaining consistent configuration across the application.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig extends BaseConfig {
    @NotBlank(message = "Timezone must not be blank")
    @Pattern(regexp = "^[A-Za-z/_]+$", message = "Invalid timezone format")
    private String timezone;

    @NotBlank(message = "Locale must not be blank")
    @Pattern(regexp = "^[a-z]{2}(_[A-Z]{2})?$", message = "Invalid locale format (e.g., pl_PL)")
    private String locale;

    @NotNull(message = "Log level must not be null")
    private LogLevel logLevel;

    @NotBlank(message = "Frontend URL must not be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid frontend URL format")
    private String frontendUrl;

    @NotBlank(message = "DOCX to PDF converter URL must not be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid DOCX to PDF converter URL format")
    private String docxToPdfConverterUrl;


    @NotNull(message = "Allowed origins must not be null")
    private List<@Pattern(regexp = "^(http|https)://.*$", message = "Invalid origin URL format") String> allowedOrigins;
}
