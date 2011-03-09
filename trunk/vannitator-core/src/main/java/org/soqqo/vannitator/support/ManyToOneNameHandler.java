package org.soqqo.vannitator.support;

import java.io.Serializable;

import org.soqqo.vannitator.annotation.VannitateManyToOne;

public class ManyToOneNameHandler extends AbstractNameHandler implements Serializable, NameHandler<VannitateManyToOne> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // hrmm, should we be supporting a generic Annotation instead ? 
    private VannitateManyToOne conf;

    public ManyToOneNameHandler(VannitateManyToOne conf) {
        this.conf = conf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.support.NameHandler#getNewPackageName(java.lang.String)
     */
    @Override
    public String getNewPackageName(String currentPackageName) {
        return currentPackageName; // different to OneToOne, we don't support package renames .. just put the marked interface where you want the replacement to come out.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.support.NameHandler#getNewSimpleClassName(java.lang.String)
     */
    @Override
    public String getNewSimpleClassName(String currentClassName) {
        return "".equals(conf.newClassName()) ? currentClassName : conf.newClassName();
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