package org.soqqo.vannitator.support;

import java.lang.annotation.Annotation;

import org.soqqo.vannitator.annotation.VannitateManyToOne;
import org.soqqo.vannitator.annotation.VannitateOneToOne;

public class NameHandlerFactory {

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> NameHandler<T> getNameHandler(T annotation) {
        if (annotation instanceof VannitateOneToOne) {
            return (NameHandler<T>) new OneToOneNameHandler((VannitateOneToOne) annotation);
        } else if (annotation instanceof VannitateManyToOne) {
            return (NameHandler<T>) new ManyToOneNameHandler((VannitateManyToOne) annotation);
        } else {
            return null;
        }

    }
}
