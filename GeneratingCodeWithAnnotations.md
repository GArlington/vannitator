# Overview #

You will need two jars.
  * vannitation-core
  * (specific annotation for your project - or make your own)
> > Currently, only GWT annotations exist.

# Annotate Your Class #

```
@GenRequestProxy(packageRename="{0-1}.client.request")
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
```


this will produce a GWT Request proxy as follows
```
/*
 * Vannitator generated from: test.samples.Foo
 * http://vanitator.soqqo.org
 * 
 * This is the EntityProxy of {@link test.samples.Foo} for the GWT RequestFactory
 */
package test.samples.client.request;

import com.google.gwt.requestfactory.shared.EntityProxy;
import com.google.gwt.requestfactory.shared.ProxyFor;

// import test.samples.Foo;



/*

   Foo --> FooProxy
   test.samples --> test.samples.client.request
   test.samples.Foo --> test.samples.client.request.FooProxy
*/

@ProxyFor(Foo.class)
public interface FooProxy extends EntityProxy {

    /*
      fields:
        int a;     
        Bar barField;     
    */
       
       void setA(int a);
       int getA();
       void setBarField(BarProxy barField);
       BarProxy getBarField();

}
```

You will notice that Bar in the Source of Foo becomes "BarProxy". This is because the Bar Class is also annotated like Foo. (For Example)
```
package test.samples;

import org.soqqo.vannitator.annotation.gwt.GenRequestProxy;

@GenRequestProxy
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
```

Because there is NO package Rename declared on the bar Annotation, the resulting BarProxy class looks like

```
/*
 * Vannitator generated from: test.samples.Bar
 * http://vanitator.soqqo.org
 * 
 * This is the EntityProxy of {@link test.samples.Bar} for the GWT RequestFactory
 */
package test.samples;

import com.google.gwt.requestfactory.shared.EntityProxy;
import com.google.gwt.requestfactory.shared.ProxyFor;

// import test.samples.Bar;



/*

   Bar --> BarProxy
   test.samples --> test.samples
   test.samples.Bar --> test.samples.BarProxy
*/

@ProxyFor(Bar.class)
public interface BarProxy extends EntityProxy {

    /*
      fields:
        int b;     
        CollectorAnnotationClass nonAnnotatedClass;     
    */
       
       void setB(int b);
       int getB();
       void setNonAnnotatedClass(CollectorAnnotationClass nonAnnotatedClass);
       CollectorAnnotationClass getNonAnnotatedClass();

}

```