package org.soqqo.vannitator.processors;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple bean which holds an annotation and it's values as represented over a
 * class and a Map of any other values that were on the annotation.
 * 
 * This object is then passed to FreeMarker for the template to play with.
 * 
 * T is the base annotation (core to the Vannitation) package.
 * 
 * @author rbuckland
 * 
 */
public class ConfigBean<T extends Annotation> implements Serializable {

    private static final long serialVersionUID = 1L;

    T conf;
    private Map<String, Serializable> params = new HashMap<String, Serializable>();

    public void setAnnotationConfig(T conf) {
        this.conf = conf;
    }

    public T getAnnotationConfig() {
        return conf;
    }

    public void addParam(String methodName, Serializable value) {
        this.params.put(methodName, value);
    }

    public Map<String, Serializable> getParams() {
        return params;
    }


}
