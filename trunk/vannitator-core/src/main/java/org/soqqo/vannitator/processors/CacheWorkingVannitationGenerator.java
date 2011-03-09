package org.soqqo.vannitator.processors;

import java.util.List;

import org.soqqo.vannitator.annotation.VannitationType;
import org.soqqo.vannitator.cache.ClassAnnotationProcessorEntry;
import org.soqqo.vannitator.cache.VannitationCrossRunCache;

/**
 * Pratically this is the vannitation generator
 * @author rbuckland
 *
 */
public class CacheWorkingVannitationGenerator {

    public static void generate(VannitationCrossRunCache cache) {
        List<ClassAnnotationProcessorEntry> allManyToOnes = cache.getAll(VannitationType.VannitateManyToOne);
        for (ClassAnnotationProcessorEntry entry : allManyToOnes) {

            if (entry.getReferencedAnnotationFQN() != null) {

                // guarantees to return entries that have classes that exist
                // (only).
                List<ClassAnnotationProcessorEntry> allOneToOnes = cache.getByAnnotationClass(entry.getReferencedAnnotationFQN());

                for (ClassAnnotationProcessorEntry toGenerate : allOneToOnes) {

                    System.out.println("need to generate for " + toGenerate.getAnnotatedFQClassName());

                    // create the vanniotated type and put it in the list
                    /*
                     * VModelBuilder builder = new VModelBuilder(env);
                     * builder.mapToVModel(supportedAnnotation,classRepresenter); 
                     * VAnnotatedType annotatedType = builder.getVAnnotatedType(supportedAnnotation, classRepresenter.getQualifiedName().toString());
                     */
                }
            }

            // call the freemarker stuff to generate

        }
    }

}
