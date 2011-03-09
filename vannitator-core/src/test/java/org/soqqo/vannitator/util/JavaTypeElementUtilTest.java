package org.soqqo.vannitator.util;

import junit.framework.TestCase;

import org.soqqo.vannitator.processors.VannitationOneToOneProcessor;
import org.soqqo.vannitator.test.util.AptRunner;

public class JavaTypeElementUtilTest extends TestCase {

    public void testTheUtil() {

        AptRunner r = new AptRunner();
        r.setFactory(VannitationOneToOneProcessor.class.getName());
        r.setSrcBasePath("src/test/java");
        r.setOutputDirectory("target/test-output");
        r.execute();
        
    }

}
