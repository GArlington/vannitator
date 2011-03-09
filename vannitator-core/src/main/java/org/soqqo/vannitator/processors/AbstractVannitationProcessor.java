package org.soqqo.vannitator.processors;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;

import org.soqqo.vannitator.annotation.VannitationType;
import org.soqqo.vannitator.cache.ClassAnnotationProcessorEntry;
import org.soqqo.vannitator.cache.MultiKeyDiskCache;
import org.soqqo.vannitator.cache.VannitationCrossRunCache;
import org.soqqo.vannitator.support.AnnotationManager;
import org.soqqo.vannitator.vmodel.VAnnotatedType;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractVannitationProcessor extends AbstractProcessor {

    protected ProcessingEnvironment env;

    protected Configuration freemarkerConfig;

    protected abstract VannitationType type();

    /**
     * We return a list of types as found in all <code>META-INF/VannitateOneToOne</code> files
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return AnnotationManager.instance().getSupportedAnnotationTypes(type());
    }

    /**
     * Call Freemarker and write out the class
     * 
     * @param annotatedClassName
     * 
     * @param supportedAnnotation
     * @param annotatedType
     * @param writer
     */
    protected void writeToFile(String annotatedClassName, HashMap<String, Object> freemarkerContext, Annotation supportedAnnotation, String templateName, Writer writer) {
        /*
         * prime freemarker with the various "bits" of the class
         */

        Template template = null;
        String debugString = annotatedClassName + " --> " + templateName;

        try {
            freemarkerConfig.setClassForTemplateLoading(supportedAnnotation.getClass(), "");
            template = freemarkerConfig.getTemplate(templateName);
            template.process(freemarkerContext, writer);

        } catch (IOException e) {
            throw new RuntimeException("Could not access the template. Is it there ?: [" + debugString + "]", e);
        } catch (TemplateException e) {
            throw new RuntimeException("Freemarker flipped out!: [" + debugString + "]", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to close the writer for the source file");
                }
            }
        }
    }

    /**
     * the cache is our "inter-run" annotated class holding area. Basically, for annotations where there is a one to one relationship.
     * 
     * @param supportedAnnotation
     * @param vannotatedType
     */
    protected void pushToCache(Annotation supportedAnnotation, VAnnotatedType vannotatedType) {

        // we need a cache that will remember our "values" across each run.
        VannitationCrossRunCache cache = new MultiKeyDiskCache();

        ClassAnnotationProcessorEntry entry = new ClassAnnotationProcessorEntry();
        entry.setAnnotationFQClassName(supportedAnnotation.annotationType().getName());
        entry.setAnnotatedFQClassName(vannotatedType.getQualifiedName().getName());
        entry.setVannitationType(type());
        entry.setVannotatedType(vannotatedType);
        //entry.setConfigurationBean(vannotatedType.getConfiguration());

        cache.put(type(), entry);
        cache.persist();

    }

}
