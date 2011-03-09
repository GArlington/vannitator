package org.soqqo.vannitator.vmodel;

import java.util.ArrayList;
import java.util.List;

public class VGenericType extends VObjectType {

    private VNameGenericType genericTypeName;

    public VGenericType(String packageName, String simpleClassName) {
        super(packageName, simpleClassName);
        this.genericTypeName = new VNameGenericType(simpleClassName);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private VObjectType rawType;

    private List<VObjectType> parameterTypes = new ArrayList<VObjectType>();

    public void addParameterType(VObjectType parameterType) {
        this.parameterTypes.add(parameterType);
        this.genericTypeName.addParameterType(parameterType.getSimpleClassName());
    }

    public List<VObjectType> getParameterTypes() {
        return parameterTypes;
    }

    public void setRawType(VObjectType rawType) {
        this.rawType = rawType;
    }

    public VObjectType getRawType() {
        return rawType;
    }

}
