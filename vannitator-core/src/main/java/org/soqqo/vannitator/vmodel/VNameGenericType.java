package org.soqqo.vannitator.vmodel;

import java.util.ArrayList;

public class VNameGenericType extends VName {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<VNameSimpleClass> parameterTypes = new ArrayList<VNameSimpleClass>();

    public VNameGenericType(String name) {
        super(name);
    }

    public void addParameterType(VNameSimpleClass typeName) {
        parameterTypes.add(typeName);
    }

    /*
     * return a new name
     */
    @Override
    public String getAsNew() {
        String myName = null;
        if (getConfigBean() != null) {
            myName = getNewSimpleClassName();
        } else {
            myName = getName();
        }

        if (this.parameterTypes.size() > 0) {
            StringBuffer newName = new StringBuffer(myName);
            newName.append("<");
            int i = 1;
            for (VNameSimpleClass type : parameterTypes) {
                newName.append(type.getAsNew());
                if (i++ < parameterTypes.size()) {
                    newName.append(",");
                }
            }
            newName.append(">");
            return newName.toString();
        } else {
            return myName;
        }

    }

}
