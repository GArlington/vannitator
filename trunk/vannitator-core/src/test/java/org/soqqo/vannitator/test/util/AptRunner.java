package org.soqqo.vannitator.test.util;

/*
 * The MIT License
 *
 * Copyright 2006-2008 The Codehaus.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DirectoryScanner;

public class AptRunner {
    // read-only parameters ---------------------------------------------------

    /**
     * The directory to run apt from when forked.
     * 
     * @parameter expression="${basedir}"
     * @required
     * @readonly
     */
    private File workingDirectory = new File(".");

    // configurable parameters ------------------------------------------------

    /**
     * Whether to run apt in a separate process.
     * 
     * @parameter default-value="false"
     */
    private boolean fork = false;

    /**
     * The apt executable to use when forked.
     * 
     * @parameter expression="${maven.apt.executable}" default-value="apt"
     */
    private String executable;

    /**
     * The initial size of the memory allocation pool when forked, for example <code>64m</code>.
     * 
     * @parameter
     */
    private String meminitial;

    /**
     * The maximum size of the memory allocation pool when forked, for example <code>128m</code>.
     * 
     * @parameter
     */
    private String maxmem;

    /**
     * Whether to show apt warnings. This is opposite to the <code>-nowarn</code> argument for apt.
     * 
     * @parameter expression="${maven.apt.showWarnings}" default-value="false"
     */
    private boolean showWarnings = false;

    /**
     * The source file encoding name, such as <code>EUC-JP</code> and <code>UTF-8</code>. If encoding is not
     * specified, the encoding <code>ISO-8859-1</code> is used rather than the platform default for reproducibility
     * reasons. This is equivalent to the <code>-encoding</code> argument for apt.
     * 
     * @parameter expression="${maven.apt.encoding}" default-value="ISO-8859-1"
     */
    private String encoding = "ISO-8859-1";

    /**
     * Whether to output information about each class loaded and each source file processed. This is equivalent to the <code>-verbose</code> argument for apt.
     * 
     * @parameter expression="${maven.apt.verbose}" default-value="false"
     */
    private boolean verbose = false;

    /**
     * Options to pass to annotation processors. These are equivalent to multiple <code>-A</code> arguments for apt.
     * 
     * @parameter
     */
    private String[] options;

    /**
     * Name of <code>AnnotationProcessorFactory</code> to use; bypasses default discovery process. This is equivalent
     * to the <code>-factory</code> argument for apt.
     * 
     * @parameter expression="${maven.apt.factory}"
     */
    private String factory;

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private String srcBasePath;
    
    private String outputDirectory = "target/test-output";
    

    private List<String> getCurrentClassPath() {

        ArrayList<String> classPathList = new ArrayList<String>();
        //Get the System Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
        URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();

        for (int i = 0; i < urls.length; i++) {
            classPathList.add(urls[i].getFile());
        }
        return classPathList;
    }

    private List<String> createArgs(Set<File> sourceFiles) {
        List<String> args = new ArrayList<String>();

        // javac arguments
        args.add("-classpath");
        args.add(StringUtils.join(getCurrentClassPath().toArray(), AptUtils.PATH_SEPARATOR));

        args.add("-sourcepath");
        args.add("src/test/java");

        args.add("-d");
        args.add(getOutputDirectory());

        if (!showWarnings) {
            args.add("-nowarn");
        }

        if (encoding != null) {
            args.add("-encoding");
            args.add(encoding);
        }

        if (verbose) {
            args.add("-verbose");
        }

        // apt arguments

        args.add("-s");
        args.add("target/test-output");

        // never compile
        args.add("-nocompile");

        if (options != null) {
            for (String option : options) {
                args.add("-A" + option.trim());
            }
        }

        if (StringUtils.isNotEmpty(factory)) {
            args.add("-factory");
            args.add(factory);
        }

        // source files

        for (File file : sourceFiles) {
            args.add(file.getAbsolutePath());
        }

        return args;
    }

    public void execute() {
        executeApt();

    }

    private Set<File> getSourceFiles(DirectoryScanner scanner) {
        HashSet<File> sources = new HashSet<File>();
        if (scanner.getIncludedFilesCount() > 0) {
            String[] files = scanner.getIncludedFiles();
            for (String file : files) {
                sources.add(new File(getSrcBasePath(),file));
            }
        }
        return sources;
    }

    private void executeApt() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(getSrcBasePath());
        ds.setIncludes(new String[]{"**/*.java"});
        ds.scan();
        Set<File> sourceFiles = getSourceFiles(ds);

        List<String> args = createArgs(sourceFiles);

        boolean success;

        if (fork) {
            success = AptUtils.invokeForked(getLog(), workingDirectory, executable, meminitial, maxmem, args);
        } else {
            success = AptUtils.invoke(getLog(), args);
        }

        if (!success) {
            throw new RuntimeException("Apt failed");
        }
    }

    private Logger getLog() {
        return logger;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setSrcBasePath(String srcBasePath) {
        this.srcBasePath = srcBasePath;
    }

    public String getSrcBasePath() {
        return srcBasePath;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }
}
