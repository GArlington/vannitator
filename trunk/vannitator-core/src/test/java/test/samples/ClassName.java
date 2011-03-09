package test.samples;

import samples.annotations.SampleOneToOne;

@SampleOneToOne
public class ClassName {

    private String aField;
    
    private Ab abField;

    public Bar getBar() {
        return null;
    }

    private int anInt;

    public void setFieldA(String fieldA) {
        this.aField = fieldA;
    }

    public String getFieldA() {
        return aField;
    }

    public void setSomeInt(int someInt) {
        this.anInt = someInt;
    }

    public int getSomeInt() {
        return anInt;
    }

    public void setAbField(Ab abField) {
        this.abField = abField;
    }

    public Ab getAbField() {
        return abField;
    }

    public class ClassFoo {

    }
}
