package org.soqqo.vannitator.support;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.soqqo.vannitator.processors.ConfigBean;

/**
 * Merges an Annotation with is parent annotation class. Creates a merged instance. Any "hang over" values from the "sub annotation" are placed
 * into a HashMap for future reference.
 * 
 * TODO (not necessary) this would be cooler if we could CGLIB a new Annotation that "is" that merge of the two instead of the HashMap way.
 * 
 * @author rbuckland
 * 
 */
public class AnnotationConfigMerger {

    /**
     * This implements serializable because it is an InvocatioHandler, which gets wrapped up in the
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(AnnotationConfigMerger.class.getName());

    private final static HashMap<Class<?>, ArrayList<String>> confAnnotationMethods = new HashMap<Class<?>, ArrayList<String>>();

    private static <T extends Annotation> void loadDeclaredMethods(Class<T> baseAnnotationClass) {
        if (!confAnnotationMethods.containsKey(baseAnnotationClass)) {
            confAnnotationMethods.put(baseAnnotationClass, new ArrayList<String>());
            for (Method m : baseAnnotationClass.getDeclaredMethods()) {
                confAnnotationMethods.get(baseAnnotationClass).add(m.getName());
            }
        }
    }

    /**
     * Returns the list of methods declared on the annotation requested.
     * this method is static, and the results are stored on the class for future reference.
     * 
     * @param <T>
     *            Annotation Class
     * @param baseAnnotationClass
     * @return List<String> method names.
     */
    public static <T extends Annotation> List<String> confAnnotationMethods(Class<T> baseAnnotationClass) {
        if (!confAnnotationMethods.containsKey(baseAnnotationClass)) {
            loadDeclaredMethods(baseAnnotationClass);
        }
        return confAnnotationMethods.get(baseAnnotationClass);
    }

    /**
     * Take and instance of an annotation, and a base AnnotationClass <T>.
     * annotatedAnnotation must be annotated with <T>.
     * 
     * This will return a configBean. Config bean will have an instance of <T>.
     * This instance will be a "union" of values of the "logically" overidden methods from annotatedAnnotation and the <T> defauls on <T> and the values as
     * default in the annotation on annotatedAnnotation. Confused?. :-) I tried.
     * 
     * The other methods that are declared on annotatedAnnotation, the name of the method, and it's value are loaded into a HashMap which
     * is accessible on ConfigBean also.
     * 
     * An Example.
     * 
     * <code>
     * 
     * @VannitationOneToOne(templateName="foo", prefix="Prefix")
     *                                          public @interface MyAnno {
     *                                          String someOther();
     *                                          String suffix() = "Suffix";
     *                                          } </code>
     * 
     *                                          This will produce a Configbean,
     *                                          - with an instance of @VannitationOneToOne.prefix() = Prefix, @VannitationOneToOne.suffix() = Suffix
     * @VannitationOneToOne.templateName() = "foo".
     *                                     - A Hashmap of someOther,[value]
     * 
     *                                     If the annotatedAnnotation instance (from the annotated Class) overrides suffix() (as it can) then @VannitationOneToOne.suffix() will return that value instead.
     * 
     * 
     * @param <T>
     * @param baseAnnotationClass
     * @param annotatedAnnotation
     * @return ConfigBean.
     */
    public static <T extends Annotation> ConfigBean<T> merge(Class<T> baseAnnotationClass, Annotation annotatedAnnotation) {

        loadDeclaredMethods(baseAnnotationClass);

        @SuppressWarnings("unchecked")
        T mergedConfig = (T) Proxy.newProxyInstance(baseAnnotationClass.getClassLoader(), new Class[] { baseAnnotationClass }, new AnnotationMergeProxyInvocationHandler<T>(baseAnnotationClass, annotatedAnnotation));
        ConfigBean<T> holder = new ConfigBean<T>();
        holder.setAnnotationConfig(mergedConfig);

        // get all the other values from the annotation
        for (Method m : annotatedAnnotation.annotationType().getDeclaredMethods()) {
            // if it is NOT a method on our @VannitateOneToOne annotation, then
            // we will run
            // it.
            if (!confAnnotationMethods.get(baseAnnotationClass).contains(m.getName())) {
                boolean invokeWorked = false;
                Object c = null;
                try {
                    c = m.invoke(annotatedAnnotation, (Object[]) null);
                    invokeWorked = true;
                    holder.addParam(m.getName(), (Serializable) c);
                } catch (Exception e) {
                    log.warning("The parameter [" + m.getName() + "] was not added to the config. " + (invokeWorked ? "value[" + c + "]" : "Failed to get the value") + e.getLocalizedMessage());
                }
            }
        }
        return holder;
    }

}
