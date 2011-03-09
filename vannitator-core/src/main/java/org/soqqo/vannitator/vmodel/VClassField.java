package org.soqqo.vannitator.vmodel;

/**
 * Represents a field on a class.
 * 
 * @author rbuckland
 * 
 */
public class VClassField extends VType {

    public VClassField(String name) {
        super(new VName(name));
    }

    private static final long serialVersionUID = 1L;

    private VValueType type;

    public void setType(VValueType type) {
        this.type = type;
    }

    public VValueType getType() {
        return type;
    }

    public String toString() {
        return getType().getName() + " " + getName();
    }
}
