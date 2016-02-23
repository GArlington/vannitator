# Overview #

There are two types of Annotations for Code Generation.

  * One to One - One Class Annotated by One Annotation, creates One new Class
  * Many to One - Many Classes Annotated by One (same) Annotation, creates One new Class

First, decide which type of "Code Generation" you want.
  * OneToOne, or
  * ManyToOne

Then it is simply a matter of making the Annotation. This is an example of the GWT requestProxy Annotation, which will create a RequestProxy Class.

# The Detail #

The magic is in the @VannitateOneToOne annotation (this is our vannitator annotation) which sets out, which [FreeMarker](http://freemarker.sourceforge.net/docs/index.html) template to use (eg: [GenRequestProxy.ftl](http://code.google.com/p/vannitator/source/browse/trunk/vannitator-gwt/src/main/resources/org/soqqo/vannitator/annotation/gwt/GenRequestProxy.ftl), and any other "defaults". Defaults are the ones specified in the annotation [VannitateOneToOne](http://code.google.com/p/vannitator/source/browse/trunk/vannitator-core/src/main/java/org/soqqo/vannitator/annotation/VannitateOneToOne.java). These are templateName, prefix, suffix and the package rename pattern.

```
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@VannitateOneToOne(templateName = "org/soqqo/vannitator/annotation/gwt/GenRequestProxy.ftl", suffix = "Proxy")
public @interface GenRequestProxy {
    /**
     * The Package rename is a simple replacement string map.
     * if the current package is <code>org.foo.bar.model<code> then each dotted package name is as a position placeholder.
     * {0} represents org, {2} is bar and so on
     * 
     * A packageRename can look like
     * 
     *  </code>{0-2}.client.request.{3}</code>
     * 
     * Which will produce
     * 
     * <code>org.foo.bar.client.request.model</code>
     * 
     * Valid values for the braces are {n}, {n-} and {n-m}
     * 
     * 
     * @return
     */
    String packageRename() default "{0-}";
}

```

Once you have created your annotation, you are then free to annotate your classes.

To see it in action, you can have a look at the [ProcessorTest](http://code.google.com/p/vannitator/source/browse/trunk/vannitator-core/src/test/java/org/soqqo/vannitator/processors/ProcessorTest.java) in the vannitation-core package.

The most important part you will need to understand is
  * What is provided to the FreeMarker Template - TheVModel
  * How do I get this Annotation to make some Code - ConfiguringIDEs?