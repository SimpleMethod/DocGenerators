package pl.mlodawski.docgenerator.pluginsystem.core.registry;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
@Author Michał Młodawski
*/
/**
 * Represents a custom annotation that marks a method as a service operation.
 * Methods annotated with {@code ServiceOperation} can be registered and executed
 * dynamically via a centralized plugin operation framework.
 *
 * The annotation requires a single parameter, {@code value}, which specifies
 * the unique identifier for the operation. This identifier is used to reference
 * the operation in the registration and execution process.
 *
 * Typically used in conjunction with service classes within a Spring application
 * where operations are registered dynamically during application initialization.
 *
 * Usage Context:
 * - Methods in a class annotated with {@link org.springframework.stereotype.Service}.
 * - Dynamically registered via infrastructure components like {@code ServiceOperationRegistrar}.
 *
 * Attributes:
 * - {@code value} (String): The unique identifier for the operation.
 *
 * Annotation Retention Policy:
 * - The annotation is retained at runtime, allowing it to be accessed
 *   via reflection during operation registration and execution.
 *
 * Target:
 * - Can only be used on methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceOperation {
    String value();
}