package pl.mlodawski.api.response.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
@Author Michał Młodawski
 */

/**
 * Annotation used to mark and configure a class as a response processor.
 * Classes annotated with {@code @ResponseProcessor} indicate their capability
 * to handle specific response types and define an execution order when multiple
 * processors are present.
 *
 * Attributes:
 * - order: Determines the precedence of the processor when multiple
 *   processors are available. The lower the value, the higher the priority.
 * - supportedTypes: Specifies the response types the processor is capable
 *   of handling. These types are determined based on the application logic.
 *
 * This annotation is intended to be applied to classes that implement
 * functionality for processing API responses, typically implementing or
 * utilizing related interfaces such as {@code ResponseHandler}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseProcessor {
    int order() default 0;
    String[] supportedTypes() default {};
}