package org.soqqo.vannitator.test.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

public class GeneralTestUtils {

    public static boolean fileContains(String filename, String stringToFind) {
        try {
            String contents = IOUtils.toString(new FileInputStream(new File(filename)));
            return (contents.contains(stringToFind));
        } catch (Exception e) {

            Assert.fail("Failed to load filename: "  + filename + " - " + e.getLocalizedMessage());
            // never gets here but hey :-) 
            return false;
            
        }

    }
}
