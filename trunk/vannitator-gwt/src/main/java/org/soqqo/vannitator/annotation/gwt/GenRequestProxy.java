package org.soqqo.vannitator.annotation.gwt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.soqqo.vannitator.annotation.VannitateOneToOne;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@VannitateOneToOne(templateName = "org/soqqo/vannitator/annotation/gwt/GenRequestProxy.ftl", suffix = "Proxy")
public @interface GenRequestProxy {
    /**
     * The Package rename is a simple replacement string map.
     * if the current package is <code>org.foo.bar.model<code> then each dotted package name is as a position placeholder.
     * {0} represents org, {2} is bar and so on
     * 
     * A packageRename can look like
     * 
     *  </code>{0-2}.client.request.{3}</code>
     * 
     * Which will produce
     * 
     * <code>org.foo.bar.client.request.model</code>
     * 
     * Valid values for the braces are {n}, {n-} and {n-m}
     * 
     * 
     * @return
     */
    String packageRename() default "{0-}";
}
