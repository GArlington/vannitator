package org.soqqo.vannitator.vmodel;

import java.io.Serializable;

public class VType implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public VType(VName name) {
        this.name = name;
    }
    
    private VName name;

    public VName getName() {
        return name;
    }

    public String toString() {
        return getName().toString();
    }

}
