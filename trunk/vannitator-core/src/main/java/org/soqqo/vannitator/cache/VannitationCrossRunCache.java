package org.soqqo.vannitator.cache;

import java.util.List;

import org.soqqo.vannitator.annotation.VannitationType;

public interface VannitationCrossRunCache {

    public abstract void put(VannitationType key, ClassAnnotationProcessorEntry value);

    public abstract void persist();

    public abstract void destroy();

    public abstract List<ClassAnnotationProcessorEntry> getAll(VannitationType key);

    /**
     * Find entries that are annotated with an annotation (by FQN)
     * 
     * @param annotationClassName
     * @return
     */
    public abstract List<ClassAnnotationProcessorEntry> getByAnnotationClass(String annotationClassName);

}