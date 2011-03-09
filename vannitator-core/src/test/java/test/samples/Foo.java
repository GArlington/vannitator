package test.samples;

import samples.annotations.SampleOneToOne;

@SampleOneToOne(packageRename="{0-1}.client.request")
public class Foo {

    private int a;

    private Bar barField;

    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setBarField(Bar barField) {
        this.barField = barField;
    }

    public Bar getBarField() {
        return barField;
    }

}
