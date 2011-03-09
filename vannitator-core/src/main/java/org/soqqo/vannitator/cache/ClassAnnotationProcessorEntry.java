package org.soqqo.vannitator.cache;

import java.io.Serializable;

import org.soqqo.vannitator.annotation.VannitationType;
import org.soqqo.vannitator.processors.ConfigBean;
import org.soqqo.vannitator.vmodel.VAnnotatedType;

/**
 * A bean which will hold the items we need to serialise through the processing
 * stages.
 * 
 * @author rbuckland
 * 
 */
public class ClassAnnotationProcessorEntry implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("rawtypes")
    public ClassAnnotationProcessorEntry(String annotationFQClassName, String annotatedFQClassName, VannitationType vannitationType, String referencedAnnotationFQN, ConfigBean configurationBean) {
        super();
        this.annotationFQClassName = annotationFQClassName;
        this.annotatedFQClassName = annotatedFQClassName;
        this.vannitationType = vannitationType;
        this.referencedAnnotationFQN = referencedAnnotationFQN;
        this.configurationBean = configurationBean;
    }

    @SuppressWarnings("rawtypes")
    public ClassAnnotationProcessorEntry(String annotationFQClassName, String annotatedFQClassName, VannitationType vannitationType, ConfigBean configurationBean) {
        super();
        this.annotationFQClassName = annotationFQClassName;
        this.annotatedFQClassName = annotatedFQClassName;
        this.vannitationType = vannitationType;
        this.configurationBean = configurationBean;
    }

    public ClassAnnotationProcessorEntry() {}

    /**
     * As far as we are concerned, two entries are the same if they point to the same class and have the same annotation.
     * We will disregard any data "actually" being the same underneath that smoke test, as it will be that one is stale. (which is kinda != but different :-) )
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassAnnotationProcessorEntry) {
            ClassAnnotationProcessorEntry otherEntry = (ClassAnnotationProcessorEntry) obj;
            return otherEntry.annotationFQClassName.equals(this.annotationFQClassName) && otherEntry.annotatedFQClassName.equals(this.annotatedFQClassName);
        } else {
            return false;
        }

    }

    public String toString() {
        return vannitationType + ":" + annotationFQClassName + "/" + annotatedFQClassName + "/ref:" + referencedAnnotationFQN + "/van:" + vannotatedType.getName();
    }

    private String annotationFQClassName;

    private String annotatedFQClassName;

    private VannitationType vannitationType;

    private VAnnotatedType vannotatedType;

    private String referencedAnnotationFQN;

    @SuppressWarnings("rawtypes")
    private ConfigBean configurationBean;

    public String getAnnotationFQClassName() {
        return annotationFQClassName;
    }

    public void setAnnotationFQClassName(String annotationFQClassName) {
        this.annotationFQClassName = annotationFQClassName;
    }

    public String getAnnotatedFQClassName() {
        return annotatedFQClassName;
    }

    public void setAnnotatedFQClassName(String annotatedFQClassName) {
        this.annotatedFQClassName = annotatedFQClassName;
    }

    public VannitationType getVannitationType() {
        return vannitationType;
    }

    public void setVannitationType(VannitationType vannitationType) {
        this.vannitationType = vannitationType;
    }

    public String getReferencedAnnotationFQN() {
        return referencedAnnotationFQN;
    }

    public void setReferencedAnnotationFQN(String referencedAnnotationFQN) {
        this.referencedAnnotationFQN = referencedAnnotationFQN;
    }

    @SuppressWarnings("rawtypes")
    public ConfigBean getConfigurationBean() {
        return configurationBean;
    }

    @SuppressWarnings("rawtypes")
    public void setConfigurationBean(ConfigBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    /**
     * @param vannotatedType
     *            the vannotatedType to set
     */
    public void setVannotatedType(VAnnotatedType vannotatedType) {
        this.vannotatedType = vannotatedType;
    }

    /**
     * @return the vannotatedType
     */
    public VAnnotatedType getVannotatedType() {
        return vannotatedType;
    }
}
