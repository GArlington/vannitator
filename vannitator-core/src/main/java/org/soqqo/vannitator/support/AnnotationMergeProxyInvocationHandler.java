package org.soqqo.vannitator.support;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Helper class which merges the annotations for us. Created by ConfMerger
 * 
 * @author rbuckland
 * 
 * @param <T>
 *            The annotation we are merging
 */
public class AnnotationMergeProxyInvocationHandler<T extends Annotation> implements InvocationHandler, Serializable {

    /**
     * We implement Serializable because we will get wrapped up in the Config Bean (the merge of annotations)
     * and end up in the persistent disk cache.
     */
    private static final long serialVersionUID = 1L;

    public AnnotationMergeProxyInvocationHandler(Class<T> baseAnnotationClass, Annotation annotatedAnnotation) {
        this.annotatedAnnotation = annotatedAnnotation;
        this.confAnnotation = this.annotatedAnnotation.annotationType().getAnnotation(baseAnnotationClass);
    }

    private Annotation confAnnotation;

    private Annotation annotatedAnnotation;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //
        // -- Comments will refer to this sample
        // @VannitateOneToOne(...)
        // public @interface Foo {
        // .. someMethodOverride();
        // }

        Object returnValue = null;

        // we will first try to invoke the same named method on our
        // @VannitateOneToOne
        // annotated Annotation
        try {
            Method alternativeAnnoMethod = annotatedAnnotation.annotationType().getMethod(method.getName(), (Class<?>[]) null);
            // see if someMethodOverride() exists on the Foo annotation
            returnValue = alternativeAnnoMethod.invoke(annotatedAnnotation, (Object[]) null);

            // not there, so now just invoke the method (which we know exists)
            // on our Annotation
        } catch (NoSuchMethodException e) {
            Method confMethod = confAnnotation.annotationType().getMethod(method.getName(), (Class<?>[]) null);
            returnValue = confMethod.invoke(confAnnotation, (Object[]) null);
        }
        return returnValue;
    }

}
