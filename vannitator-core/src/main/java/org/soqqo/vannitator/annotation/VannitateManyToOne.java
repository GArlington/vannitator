package org.soqqo.vannitator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark an Annotation with this annotation Then use THAT annotation to mark a
 * class.
 * 
 * @author rbuckland
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface VannitateManyToOne {

    /**
     * The fully qualified name of the annotation for which we want a VModel of,
     * supplied to the template The FreeMarker Template will be provided with a
     * list of VAnnotatedType VTypes which will be all classes that are
     * annotated with the annotation as declared. Would have been nice to accept
     * an annotation here.
     * 
     * @return
     */
    String annotation();

    /**
     * Name of the template to use for this class
     * 
     * @return
     */
    String templateName();

    /**
     * What is the new classname
     * 
     * @return
     */
    String newClassName() default "";

}
