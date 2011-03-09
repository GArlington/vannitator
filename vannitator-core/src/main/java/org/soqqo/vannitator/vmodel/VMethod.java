package org.soqqo.vannitator.vmodel;

import java.util.ArrayList;
import java.util.List;

public class VMethod extends VType {

    public VMethod(String name) {
        super(new VName(name));
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    VType returnType;

    List<VMethodParameter> parameters = new ArrayList<VMethodParameter>();

    public VType getReturnType() {
        return returnType;
    }

    public void setReturnType(VType returnType) {
        this.returnType = returnType;
    }

    public List<VMethodParameter> getParameters() {
        return parameters;
    }

    public void addParameter(VMethodParameter parameter) {
        this.parameters.add(parameter);
    }

    public List<VType> getAllDependantTypes() {
        ArrayList<VType> types = new ArrayList<VType>();

        // method return type
        types.add(returnType);

        // parameters
        for (VMethodParameter param : parameters) {
            types.add(param.getType());
        }
        return types;

    }
    
    public String getAsNew() {
        StringBuffer x = new StringBuffer();
        x.append(returnType.getName().getAsNew()).append(" ").append(getName().getAsNew());
        x.append("(");
        for (VMethodParameter param : parameters) {
            x.append(param.toString()).append(",");
        }
        if (parameters.size() > 0) {
            x.setLength(x.length() - 1);
        }
        x.append(")");
        return x.toString();
    }

    public String toString() {
        StringBuffer x = new StringBuffer();
        x.append(returnType.getName()).append(" ").append(getName());
        x.append("(");
        for (VMethodParameter param : parameters) {
            x.append(param.toString()).append(",");
        }
        if (parameters.size() > 0) {
            x.setLength(x.length() - 1);
        }
        x.append(")");
        return x.toString();
    }

}
