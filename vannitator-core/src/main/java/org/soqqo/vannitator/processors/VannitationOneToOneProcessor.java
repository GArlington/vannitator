package org.soqqo.vannitator.processors;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.soqqo.vannitator.annotation.VannitateManyToOne;
import org.soqqo.vannitator.annotation.VannitateOneToOne;
import org.soqqo.vannitator.annotation.VannitationType;
import org.soqqo.vannitator.cache.MultiKeyDiskCache;
import org.soqqo.vannitator.vmodel.VAnnotatedType;
import org.soqqo.vannitator.vmodel.builder.VModelBuilder;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * Processes {@link Vanitation} annotations.
 * <p/>
 * {@link VannitationOneToOneProcessor} should only ever be called by tool infrastructure. See {@link javax.annotation.processing.Processor} for more details.
 * 
 * Idea off a similar but specific Annotation processor @GenDto http://code.google.com/p/gwt-platform
 * 
 * @author Ramon Buckland
 */

@SupportedSourceVersion(RELEASE_6)
public class VannitationOneToOneProcessor extends AbstractVannitationProcessor {

    Logger logger = Logger.getLogger(VannitationOneToOneProcessor.class.getName());

    @Override
    protected VannitationType type() {
        return VannitationType.VannitateOneToOne;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.env = env;
        try {
            freemarkerConfig = new Configuration();
            freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
        } catch (Exception e) {
            throw new RuntimeException("Failed to init Freemarker", e);
        }
    }

    /**
     * Process a given set of Annotations
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (TypeElement currAnnotation : annotations) {
                String annotationFQN = currAnnotation.getQualifiedName().toString();
                if (getSupportedAnnotationTypes().contains(annotationFQN)) {
                    // it is one of our annotations. The processing environment in Eclipse will only have one class in the ElementsAnnotatedWith() set.
                    for (Element annotatedClass : roundEnv.getElementsAnnotatedWith(currAnnotation)) {
                        try {
                            @SuppressWarnings("unchecked")
                            Annotation supportedAnnotation = annotatedClass.getAnnotation((Class<Annotation>) Class.forName(annotationFQN));
                            logger.fine("Calling one to one: " + annotationFQN + " for " + annotatedClass.getSimpleName());
                            this.generateOneToOneClassFile(supportedAnnotation, (TypeElement) annotatedClass);
                        } catch (ClassNotFoundException e) {
                            logger.log(Level.WARNING, "Annotation [" + annotationFQN + "] was not found. (is Annotating [" + ((TypeElement) annotatedClass).getQualifiedName().toString() + "] )");
                        }

                    }

                    // now generate the files that may depend on this added entry.
                    CacheWorkingVannitationGenerator.generate(new MultiKeyDiskCache());
                }
            }
        }
        return true;
    }

    /**
     * Create a single class from one other
     * 
     * @param supportedAnnotation
     * @param annotatedClass
     */
    void generateOneToOneClassFile(Annotation supportedAnnotation, TypeElement annotatedClass) {

        // load the model
        VModelBuilder<VannitateOneToOne> builder = new VModelBuilder<VannitateOneToOne>(env);
        builder.mapToVModel(VannitateOneToOne.class, supportedAnnotation, annotatedClass);
        VAnnotatedType<VannitateOneToOne> vannotatedType = builder.getVAnnotatedType(supportedAnnotation, annotatedClass.getQualifiedName().toString());

        // we need to put this class into the cache and persist it.
        // we (as the OneToOne processor don't need it (the cache) but the
        // ManyToOne may.

        pushToCache(supportedAnnotation, vannotatedType);

        // get a writer to the src file
        Writer writer = null;
        try {
            // writer = this.env.getFiler().createResource(Location, pkg,
            // relativeName, originatingElements)
            writer = this.env.getFiler().createSourceFile(vannotatedType.getQualifiedName().getAsNew(), annotatedClass).openWriter();
        } catch (IOException e1) {
            throw new RuntimeException("Failed to create the source file for: " + vannotatedType.getQualifiedName().getAsNew(), e1);
        }

        // create the freemarker context object (slightly different for one to one) 
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("vtype", vannotatedType);
        context.put("params", vannotatedType.getConfiguration().getParams());
        context.put("conf", vannotatedType.getConfiguration().getAnnotationConfig());

        writeToFile(vannotatedType.getQualifiedName().getName(), context, supportedAnnotation, vannotatedType.getConfiguration().getAnnotationConfig().templateName(), writer);
    }

}
