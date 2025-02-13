package pl.mlodawski.docgenerator.utils.filemanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
@Author Michał Młodawski
*/
/**
 * Annotation used to designate a field as a template resource.
 *
 * The @TemplateResource annotation provides metadata for locating and loading
 * template files. This annotation is processed during the post-initialization
 * phase of the Spring bean lifecycle, allowing the specified fields to be
 * populated with the content of the corresponding template file.
 *
 * The "module" and "path" attributes define the module directory and specific
 * file path within the templates directory, respectively. The location of the
 * base templates directory is determined by the TemplateConfiguration class.
 *
 * Attributes:
 * - {@code path}: Specifies the relative file path of the resource within the module directory.
 * - {@code module}: Specifies the module directory where the template resource is located.
 *
 * This annotation is retained at runtime and can only be applied to fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TemplateResource {
    String path();
    String module();
}
