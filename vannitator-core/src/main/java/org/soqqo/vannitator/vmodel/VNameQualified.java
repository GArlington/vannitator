package org.soqqo.vannitator.vmodel;

public class VNameQualified extends VName {

    private static final long serialVersionUID = 1L;

    private String className;
    private String packageName;

    public VNameQualified(String className, String packageName) {
        super(packageName.equals("") ? className : packageName + "." + className);
        this.className = className;
        this.packageName = packageName;
    }

    /*
     * return a new name
     */
    @Override
    public String getAsNew() {
        if (getConfigBean() != null) {
            return getNameHandler().getNewQualifiedName(className, packageName);
        } else {
            return getName();
        }
    }

}
