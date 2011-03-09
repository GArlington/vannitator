package org.soqqo.vannitator.support;

import java.io.Serializable;

import org.soqqo.vannitator.annotation.VannitateOneToOne;

public class OneToOneNameHandler extends AbstractNameHandler implements Serializable, NameHandler<VannitateOneToOne> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // hrmm, should we be supporting a generic Annotation instead ? 
    private VannitateOneToOne conf;

    public OneToOneNameHandler(VannitateOneToOne conf) {
        this.conf = conf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.support.NameHandler#getNewPackageName(java.lang.String)
     */
    @Override
    public String getNewPackageName(String currentPackageName) {
        return utilGetNewPackageName(conf.packageRename(), currentPackageName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.support.NameHandler#getNewSimpleClassName(java.lang.String)
     */
    @Override
    public String getNewSimpleClassName(String currentClassName) {
        return conf.prefix() + currentClassName + conf.suffix();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.support.NameHandler#getNewQualifiedName(java.lang.String, java.lang.String)
     */
    @Override
    public String getNewQualifiedName(String currentClassName, String currentPackageName) {
        if ("".equals(currentPackageName)) {
            return getNewSimpleClassName(currentClassName);
        } else {
            return getNewPackageName(currentPackageName) + "." + getNewSimpleClassName(currentClassName);
        }
    }
}