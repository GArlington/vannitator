package org.soqqo.vannitator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface VannitateOneToOne {

    /**
     * Name of the template to use for this class
     * 
     * @return
     */
    String templateName();

    /**
     * A prefix on the class to be generated
     * 
     * @return
     */
    String prefix() default "";

    /**
     * A suffix on the class to be generated
     * 
     * @return
     */
    String suffix() default "Dto";

    /**
     * The Package rename is a simple replacement string map.
     * if the current package is <code>org.foo.bar.model<code> then each dotted package name is as a position placeholder.
     * {0} represents org, {2} is bar and so on
     * 
     * A packageRename can look like
     * 
     *  </code>{0-2}.client.request.{3}</code>
     *  
     *  Which will produce 
     *  
     *  <code>org.foo.bar.client.request.model</code>
     *  
     *  Valid values for the braces are {n}, {n-} and {n-m}
     * 
     * 
     * @return
     */
    String packageRename() default "{0-}";

}
