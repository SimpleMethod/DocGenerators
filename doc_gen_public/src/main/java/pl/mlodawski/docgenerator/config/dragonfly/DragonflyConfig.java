package pl.mlodawski.docgenerator.config.dragonfly;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.mlodawski.docgenerator.config.base.BaseConfig;

/*
 @Author Michał Młodawski
 */
/**
 * DragonflyConfig represents the configuration settings required for Dragonfly connectivity.
 * This class extends {@code BaseConfig}, adding specific properties and validation rules
 * for configuring Dragonfly connections and related behaviors.
 *
 * Key Features:
 * - Provides fields for standard Dragonfly connection settings such as host, port, and password
 * - Includes enhanced connection management properties like retry attempts and timeout settings
 * - Adds validation annotations to enforce input constraints for each property
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DragonflyConfig extends BaseConfig {
    @NotBlank(message = "Dragonfly host is required")
    private String host;

    @NotNull(message = "Dragonfly port is required")
    @Min(value = 1, message = "Port must be greater than 0")
    private Integer port;

    @NotNull(message = "Database index is required")
    @Min(value = 0, message = "Database index must be non-negative")
    private Integer database;

    private String password;

    @NotNull
    @Min(1)
    private Integer maxRetryAttempts;

    @NotNull
    @Min(1000)
    private Integer retryDelayMs;

    @NotNull
    @Min(1000)
    private Integer connectionTimeoutMs;

    @NotNull
    @Min(1)
    private Integer maxPoolSize;

    @NotNull
    @Min(1)
    private Integer minPoolSize;
}
