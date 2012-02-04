package testproject;

import org.soqqo.vannitator.annotation.gwt.GenRequestProxy;


@GenRequestProxy 
public class FooClassSample extends AbstractClass {

    private int intField;

    private String stringField;

    /**
     * @param stringField
     *            the stringField to set
     */
    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    /**
     * @return the stringField
     */
    public String getStringField() {
        return stringField;
    }

    /**
     * @param intField
     *            the intField to set
     */
    public void setIntField(int intField) {
        this.intField = intField;
    }

    /**
     * @return the intField
     */
    public int getIntField() {
        return intField;
    }
}
