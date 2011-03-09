package org.soqqo.vannitator.vmodel;

public class VNamePackage extends VName {

    private static final long serialVersionUID = 1L;

    public VNamePackage(String name) {
        super(name);
    }

    /*
     * return a new name
     */
    @Override
    public String getAsNew() {
        if (getConfigBean() != null) {
            return getNameHandler().getNewPackageName(getName());
        } else {
            return getName();
        }
    }
}
