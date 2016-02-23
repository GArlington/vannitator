This Project is like the XDoclet of old.

It utilises Java Annotations:

> Annotate a Java Class  --> Generates a Java File (or some other artefact).

It utilises Freemarker as the template.

The annotation used "collects" information about your annotated class and this information is provided to Freemarker as a "Code Model".

It started as an idea I had when working GWT.  The very first "as shipped" annotation is a pair for GWT 2

@GenProxy and @GenRequest

which will generate the relevant Domain object proxies and RequestFactory entries.


This was based off the similar, project specific, annotation generation found in gwtp.

http://code.google.com/p/gwt-platform/wiki/BoilerplateGeneration

(This is currently very alpha - was just a theory)
