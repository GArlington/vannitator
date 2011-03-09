package test.samples;

import samples.annotations.SampleOneToOne;

@SampleOneToOne
public class Bar {
    private int b;
    
    private CollectorAnnotationClass nonAnnotatedClass;

    public void setB(int b) {
        this.b = b;
    }

    public int getB() {
        return b;
    }

    public void setNonAnnotatedClass(CollectorAnnotationClass nonAnnotatedClass) {
        this.nonAnnotatedClass = nonAnnotatedClass;
    }

    public CollectorAnnotationClass getNonAnnotatedClass() {
        return nonAnnotatedClass;
    }
}
