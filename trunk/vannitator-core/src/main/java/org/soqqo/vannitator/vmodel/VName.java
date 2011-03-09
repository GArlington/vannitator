package org.soqqo.vannitator.vmodel;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.soqqo.vannitator.annotation.VannitateOneToOne;
import org.soqqo.vannitator.processors.ConfigBean;
import org.soqqo.vannitator.support.NameHandler;
import org.soqqo.vannitator.support.NameHandlerFactory;
import org.soqqo.vannitator.support.OneToOneNameHandler;

public class VName<T extends Annotation> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    public VName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public String getAsNew() {
        return getName();
    }

    private ConfigBean<T> configBean;

    private NameHandler<T> nameHandler;

    /**
     * @param configBean
     *            the configBean to set
     */
    public void setConfigBean(ConfigBean<T> configBean) {
        this.configBean = configBean;
        this.nameHandler = NameHandlerFactory.getNameHandler(configBean.getAnnotationConfig());
    }

    /**
     * @return the configBean
     */
    public ConfigBean<T> getConfigBean() {
        return configBean;
    }
    

    public String getNewSimpleClassName() {
        return this.nameHandler.getNewSimpleClassName(name);
    }

    protected NameHandler getNameHandler() {
        return nameHandler;
    }

}
