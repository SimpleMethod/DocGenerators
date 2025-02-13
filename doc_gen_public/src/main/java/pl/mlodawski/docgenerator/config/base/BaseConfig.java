package pl.mlodawski.docgenerator.config.base;

import lombok.Data;

/*
 @Author Michał Młodawski
 */
/**
 * BaseConfig is an abstract class that serves as a foundational structure for application configuration.
 * It is designed to be extended by other configuration classes to enforce common patterns
 * and facilitate the implementation of validation logic.
 *
 * This class provides a blueprint for building configurations, including necessary methods
 * for validating configuration constraints, ensuring consistency and correctness of the settings
 * across the application.
 *
 * Subclasses are expected to override or implement specific fields, validation logic, and any
 * other custom behaviors required for their use case.
 *
 * Key Features:
 * - Can be extended by specific configuration classes to centralize and reuse common behavior.
 * - Centralizes configuration-related validation functionality.
 * - Typically used in scenarios requiring hierarchical or structured configuration.
 *
 * Expected Behavior:
 * - Subclasses should implement a `validate` method to perform custom validation logic on their fields.
 * - Any invariants or conditions specific to the subclass configuration must be checked in the `validate` method.
 *
 * Use Cases:
 * - Defining modular configuration classes such as database settings, metrics configurations,
 *   or task execution properties with consistent validation behaviors.
 */
@Data
public abstract class BaseConfig {
}
