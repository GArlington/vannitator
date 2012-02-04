package test.samples;

import samples.annotations.SampleOneToOne;

@SuppressWarnings("unused")
@SampleOneToOne
public class FooTestClass {

    static {
        int answer = 42;
    }

    int otherAnswer = -42;

    private String furniture;
    {
        this.furniture = "Table";
    }

    public FooTestClass() {
        this.otherAnswer = 42;
    }

    public void method() {
        // do nothing;
    }

    public int getAnAnswer() {
        return otherAnswer;
    }

    public FooTestClass getMe() {
        try {
            return (FooTestClass) this.clone();
        } catch (Exception e) {
            return null;
        }
    }
}
