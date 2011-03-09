package org.soqqo.vannitator.vmodel;

public class VMethodParameter extends VType {

    public VMethodParameter(String name) {
        super(new VName(name));
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private VValueType type;


    public void setType(VValueType type) {
        this.type = type;
    }

    public VValueType getType() {
        return type;
    }
    
    public String toString() {
        return type.getName().getAsNew() + " " + getName();
    }
}
