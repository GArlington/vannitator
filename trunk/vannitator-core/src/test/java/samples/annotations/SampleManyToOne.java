package samples.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.soqqo.vannitator.annotation.VannitateManyToOne;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@VannitateManyToOne(annotation = "samples.annotations.SampleOneToOne", templateName = "samples/SampleManyToOne.ftl")
public @interface SampleManyToOne {

    /**
     * The name of your request factory
     */
    String newClassName();

}
