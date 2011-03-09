package org.soqqo.vannitator.processors;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.soqqo.vannitator.annotation.VannitateManyToOne;
import org.soqqo.vannitator.annotation.VannitationType;
import org.soqqo.vannitator.cache.ClassAnnotationProcessorEntry;
import org.soqqo.vannitator.cache.MultiKeyDiskCache;
import org.soqqo.vannitator.cache.VannitationCrossRunCache;
import org.soqqo.vannitator.support.AnnotationManager;
import org.soqqo.vannitator.vmodel.VAnnotatedType;
import org.soqqo.vannitator.vmodel.builder.VModelBuilder;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * Processes {@link Vannitation} annotations.
 * <p/>
 * {@link VannitationManyToOneProcessor} should only ever be called by tool infrastructure. See {@link javax.annotation.processing.Processor} for more details.
 * 
 * Idea off a similar but specific Annotation processor @GenDto http://code.google.com/p/gwt-platform
 * 
 * @author Ramon Buckland
 */

@SupportedSourceVersion(RELEASE_6)
public class VannitationManyToOneProcessor extends AbstractVannitationProcessor {

    Logger logger = Logger.getLogger(VannitationManyToOneProcessor.class.getName());

    @Override
    protected VannitationType type() {
        return VannitationType.VannitateManyToOne;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {

        super.init(env);

        System.out.println("init called");
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

                    for (Element annotatedClass : roundEnv.getElementsAnnotatedWith(currAnnotation)) {
                        try {
                            @SuppressWarnings("unchecked")
                            Annotation supportedAnnotation = annotatedClass.getAnnotation((Class<Annotation>) Class.forName(annotationFQN));
                            generateManyToOneClassFile(supportedAnnotation, (TypeElement) annotatedClass);
                        } catch (ClassNotFoundException e) {
                            logger.log(Level.SEVERE, "Failed to load the annotation class:", e);
                        }

                    }

                }
            }
        }
        return true;
    }

    private void generateManyToOneClassFile(Annotation supportedAnnotation, TypeElement annotatedClass) {

        // 1. find all classes annotated with VannitationManyToOne.annotation()
        //    This involves dipping into the cache of all other entries and 

        // load the model
        VModelBuilder<VannitateManyToOne> builder = new VModelBuilder<VannitateManyToOne>(env);
        builder.mapToVModel(VannitateManyToOne.class, supportedAnnotation, annotatedClass);
        VAnnotatedType<VannitateManyToOne> vannotatedType = builder.getVAnnotatedType(supportedAnnotation, annotatedClass.getQualifiedName().toString());

        pushToCache(supportedAnnotation, vannotatedType);
        /*
        ConfigBean<VannitateManyToOne> cfgBean = AnnotationManager.instance().getConfAnnotation(VannitateManyToOne.class, annotatedClass.getQualifiedName().toString(),
                supportedAnnotation.annotationType().getName());
        String interestedAnnotation = cfgBean.getAnnotationConfig().annotation();
        */
        // load from cache 
        VannitationCrossRunCache cache = new MultiKeyDiskCache();
        List<ClassAnnotationProcessorEntry> entries = cache.getByAnnotationClass(vannotatedType.getConfiguration().getAnnotationConfig().annotation());

        ArrayList<VAnnotatedType<VannitateManyToOne>> vannotatedTypes = new ArrayList<VAnnotatedType<VannitateManyToOne>>();
        for (ClassAnnotationProcessorEntry entry : entries) {
            vannotatedTypes.add(entry.getVannotatedType());
        }

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
        context.put("vtypes", vannotatedTypes);
        context.put("params", vannotatedType.getConfiguration().getParams());
        context.put("conf", vannotatedType.getConfiguration().getAnnotationConfig());

        writeToFile(vannotatedType.getQualifiedName().getName(), context, supportedAnnotation, vannotatedType.getConfiguration().getAnnotationConfig().templateName(), writer);

    }
}
