package org.soqqo.vannitator.processors;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;
import org.soqqo.vannitator.test.util.GeneralTestUtils;

public class ProcessorTest {

    @Test
    public void fullComprehensiveTest() {

        // Get an instance of java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Get a new instance of the standard file manager implementation
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<String> options = new ArrayList<String>();
        options.add("-d");
        File outputDir = new File("target", "processor-test");
        outputDir.mkdirs();
        options.add(outputDir.getAbsolutePath());

        options.add("-s");
        options.add(outputDir.getAbsolutePath());

        // Get the list of java file objects
        SrcFilesTestClass srcFiles = new SrcFilesTestClass();

        System.out.println(">> testing: files to run annotation test on (only some have annotations): ");
        for (String f : srcFiles.srcFiles()) {
            System.out.println(f);
        }

        Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjects(srcFiles.srcFiles());

        StringWriter output = new StringWriter();
        CompilationTask task = compiler.getTask(output, fileManager, null, options, null, compilationUnits1);

        // Create a list to hold annotation processors
        LinkedList<AbstractProcessor> processors = new LinkedList<AbstractProcessor>();

        // Add an annotation processor to the list
        processors.add(new VannitationOneToOneProcessor());
        processors.add(new VannitationManyToOneProcessor());

        // Set the annotation processor to the compiler task
        task.setProcessors(processors);

        // Perform the compilation task.
        // the compiler will return false for us because the files we are
        // creating won't compile as
        // we don't have the required fields.
        task.call();

        // now some tests .. we will just validate that
        // the Annotation processor created the files we need (expect)

        String commonPrefix = "target/processor-test/test/samples/";
        String missing = "missing an expected, generated, file";
        Assert.assertTrue(missing, new File(commonPrefix + "client/request/FooProxy.java").exists());
        Assert.assertTrue(missing, new File(commonPrefix + "BarProxy.java").exists());
        Assert.assertTrue(missing, new File(commonPrefix + "FooTestClassProxy.java").exists());
        Assert.assertTrue(missing, new File(commonPrefix + "AbProxy.java").exists());
        Assert.assertTrue(missing, new File(commonPrefix + "AProxy.java").exists());
        Assert.assertTrue(missing, new File(commonPrefix + "ClassNameProxy.java").exists());

        // check the file .. read in some stuff and look for strings
        // had specific issues with these
        Assert.assertTrue("incorrect package", GeneralTestUtils.fileContains(commonPrefix + "MyApplicationFactory.java", "package test.samples;"));
        Assert.assertTrue("incorrect package", GeneralTestUtils.fileContains(commonPrefix + "client/request/FooProxy.java", "package test.samples.client.request;"));
        Assert.assertTrue("a proxy was not renamed", GeneralTestUtils.fileContains(commonPrefix + "client/request/FooProxy.java", "void setBarField(BarProxy barField);"));

    }
}
