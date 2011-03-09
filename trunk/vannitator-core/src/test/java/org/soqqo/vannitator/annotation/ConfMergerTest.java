package org.soqqo.vannitator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.soqqo.vannitator.processors.ConfigBean;
import org.soqqo.vannitator.support.AnnotationConfigMerger;

public class ConfMergerTest extends TestCase {

    @Extended(prefix = "Foo", number = 42)
    @ExtendedMany
    public class Sample {

    }

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @VannitateOneToOne(templateName = "foobar")
    public @interface Extended {
        String customValue() default "dd";

        int number() default 55;

        String prefix() default "Prefix";
    }

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @VannitateManyToOne(templateName = "foobar",annotation="a.class.that.we.are.interested.In")
    public @interface ExtendedMany {
    }

    public void testExtendedOneToOne() {

        Extended extended = Sample.class.getAnnotation(Extended.class);
        // extended annotation

        assertTrue(extended.prefix().equals("Foo"));
        assertTrue(extended != null);

        VannitateOneToOne c = extended.annotationType().getAnnotation(VannitateOneToOne.class);
        assertTrue(c != null);
        System.out.println(confToString(c));

        ConfigBean<VannitateOneToOne> config = AnnotationConfigMerger.merge(VannitateOneToOne.class, extended);
        dumpConfHolder(config);
        assertTrue(config.getAnnotationConfig().templateName().equals("foobar"));
        assertTrue(config.getParams().get("number").equals(42));
    }
    
    public void testExtendedManyToOne() {

        ExtendedMany extended = Sample.class.getAnnotation(ExtendedMany.class);
        assertTrue(extended != null);

        VannitateManyToOne c = extended.annotationType().getAnnotation(VannitateManyToOne.class);
        assertTrue(c != null);
        //System.out.println(confToString(c));

        ConfigBean<VannitateManyToOne> config = AnnotationConfigMerger.merge(VannitateManyToOne.class, extended);
        //dumpConfHolder(config);
        assertTrue(config.getAnnotationConfig().annotation().equals("a.class.that.we.are.interested.In"));
    }    

    public void dumpConfHolder(ConfigBean<VannitateOneToOne> holder) {
        System.out.println(holder.getAnnotationConfig().annotationType());
        System.out.println("@VannitateOneToOne values[" + confToString(holder.getAnnotationConfig()) + "] @Anno-Values[" + holder.getParams() + "]");

    }

    public String confToString(VannitateOneToOne c) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String methodName : AnnotationConfigMerger.confAnnotationMethods(VannitateOneToOne.class)) {
            try {
                Method m = c.getClass().getMethod(methodName, (Class<?>[]) null);
                System.out.println(m.getName());
                sb.append(m.getName()).append("=").append(m.invoke(c, (Object[]) null).toString()).append(",");
            } catch (Exception e) {
                // e.printStackTrace();
                sb.append("?" + e.getLocalizedMessage());
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
