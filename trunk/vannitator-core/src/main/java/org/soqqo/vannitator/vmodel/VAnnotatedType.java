package org.soqqo.vannitator.vmodel;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.soqqo.vannitator.processors.ConfigBean;

public class VAnnotatedType<T extends Annotation> extends VObjectType {

    public VAnnotatedType(ConfigBean<T> configBean, String packageName, String simpleClassName) {
        super(packageName, simpleClassName);
        this.setConfiguration(configBean);
        this.getPackageName().setConfigBean(configBean);
        this.getSimpleClassName().setConfigBean(configBean);
        this.getQualifiedName().setConfigBean(configBean);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<VMethod> methods;

    private ConfigBean<T> configuration;

    private List<VClassField> fields;

    public List<String> getDependantTypes() {
        return generateDependantTypes(true);
    }

    public List<String> getDependantTypesOriginalNames() {
        return generateDependantTypes(false);
    }

    private String determineImport(VType type, boolean asNew) {

        if (type instanceof VAnnotatedType) {
            VAnnotatedType theType = (VAnnotatedType) type;
            if (!theType.getPackageName().getName().equals(getPackageName().getName())) {

                return (asNew ? theType.getQualifiedName().getAsNew() : theType.getQualifiedName().getName());

            } else {
                return null;// ignored if it is our package
            }

        } else if (type instanceof VNonAnnotatedType) {
            VNonAnnotatedType theType = (VNonAnnotatedType) type;
            if (!theType.getPackageName().getName().equals(getPackageName().getName())) {
                return theType.getQualifiedName().getName();

                // should also check for the default java imports :-) bah!
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<String> generateDependantTypes(boolean asNew) {
        ArrayList<String> packages = new ArrayList<String>();

        // self
        // packages.add(getPackageName().getName());

        // fields
        for (VClassField field : getFields()) {

            String importable = determineImport(field.getType(), asNew);
            if (importable != null) {
                packages.add(importable);
            }

        }

        // methods
        for (VMethod method : getMethods()) {
            for (VType field : method.getAllDependantTypes()) {
                String importable = determineImport(field, asNew);
                if (importable != null) {
                    packages.add(importable);
                }
            }
        }

        // get the unique values
        Set<String> union = new HashSet<String>(packages);
        union.addAll(new HashSet<String>(packages));

        return new ArrayList<String>(union);

    }

    public String toString() {
        StringBuffer x = new StringBuffer();
        x.append("class ").append(getQualifiedName()).append(" {");
        // fields
        for (VClassField field : getFields()) {
            x.append(field.toString());
        }

        // methods
        for (VMethod method : getMethods()) {
            x.append(method.toString());
        }
        x.append("}");
        return x.toString();
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public void setConfiguration(ConfigBean<T> configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the configuration
     */
    public ConfigBean<T> getConfiguration() {
        return configuration;
    }

    /**
     * @param methods
     *            the methods to set
     */
    public void setMethods(List<VMethod> methods) {
        this.methods = methods;
    }

    /**
     * @return the methods
     */
    public List<VMethod> getMethods() {
        return methods;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(List<VClassField> fields) {
        this.fields = fields;
    }

    /**
     * @return the fields
     */
    public List<VClassField> getFields() {
        return fields;
    }

}
