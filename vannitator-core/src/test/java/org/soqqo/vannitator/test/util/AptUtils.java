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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Provides methods of invoking the Annotation Processing Tool (apt) compiler.
 * 
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @version $Id: AptUtils.java 11286 2009-11-21 16:02:42Z bentmann $
 */
public final class AptUtils {
    // constants --------------------------------------------------------------

    /**
     * The line separator for the current platform.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    // fields -----------------------------------------------------------------

    // constructors -----------------------------------------------------------

    private AptUtils() {
        throw new AssertionError();
    }

    // public methods ---------------------------------------------------------

    public static boolean invoke(Logger log, List<String> args) throws RuntimeException {
        // get apt method

        Class<?> apt = getAptClass();

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);
        String[] argsArray = args.toArray(new String[args.size()]);

        Method method;
        Object[] methodArgs;

        try {
            method = apt.getMethod("process", new Class[] { PrintWriter.class, String[].class });

            methodArgs = new Object[] { writer, argsArray };
        } catch (NoSuchMethodException exception) {
            try {
                method = apt.getMethod("compile", new Class[] { String[].class, PrintWriter.class });

                methodArgs = new Object[] { argsArray, writer };
            } catch (NoSuchMethodException exception2) {
                throw new RuntimeException("Error while executing the apt compiler", exception2);
            }
        }

        // invoke apt

        log.log(Level.FINE, "Invoking apt with arguments: " + args);

        int result;

        try {
            result = ((Integer) method.invoke(null, methodArgs)).intValue();
        } catch (IllegalAccessException exception) {
            throw new RuntimeException("Error while executing the apt compiler", exception);
        } catch (InvocationTargetException exception) {
            throw new RuntimeException("Error while executing the apt compiler", exception);
        }

        // log output

        log.log(Level.WARNING, stringWriter.toString());

        // log result

        log.log(Level.FINE, "Apt returned " + result);

        return (result == 0);
    }

    private static OutputStream createOutputStream() {
        return new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b);
            }

            public String toString() {
                return this.string.toString();
            }
        };
    }

    public static boolean invokeForked(Logger log, File workingDirectory, String executable, String meminitial, String maxmemory, List<String> args) throws RuntimeException {
        // create command

        CommandLine cmdLine = new CommandLine(executable);
        DefaultExecutor executor = new DefaultExecutor();

        log.log(Level.FINE, "Using working directory " + workingDirectory.getAbsolutePath());

        if (meminitial != null && !"".equals(meminitial)) {
            cmdLine.addArgument("-J-Xms" + meminitial);
        }

        if (maxmemory != null && !"".equals(maxmemory)) {
            cmdLine.addArgument("-J-Xmx" + maxmemory);
        }

        // create arguments file

        File argsFile;

        try {
            argsFile = createArgsFile(args);
            String argsPath = argsFile.getCanonicalPath().replace(File.separatorChar, '/');
            cmdLine.addArgument("@" + argsPath);
        } catch (IOException exception) {
            throw new RuntimeException("Error creating arguments file", exception);
        }

        log.log(Level.FINE, "Using argument file: " + argsFile);

        // invoke apt

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Invoking apt with command " + cmdLine);
        }

        OutputStream out = createOutputStream();
        OutputStream err = createOutputStream();
        executor.setWorkingDirectory(workingDirectory);

        PumpStreamHandler outHandler = new PumpStreamHandler(out, err);
        executor.setStreamHandler(outHandler);

        int result;

        try {

            result = executor.execute(cmdLine);
        } catch (ExecuteException exception) {
            throw new RuntimeException("Error while executing the apt compiler", exception);
        } catch (IOException e) {
            throw new RuntimeException("Error while executing the apt compiler", e);
        }

        // log output

        log.log(Level.INFO, out.toString());
        log.log(Level.WARNING, err.toString());

        // log result

        log.log(Level.FINE, "Apt returned " + result);

        return (result == 0);
    }

    // private methods --------------------------------------------------------

    private static Class<?> getAptClass() throws RuntimeException {
        try {
            return Class.forName("com.sun.tools.apt.Main");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("Unable to locate the apt compiler in:" + LINE_SEPARATOR + "  " + LINE_SEPARATOR + "Please ensure you are using JDK 1.5 or above and" + LINE_SEPARATOR + "not a JRE (the com.sun.tools.apt.Main class is required)." + LINE_SEPARATOR + "In most cases you can change the location of your Java" + LINE_SEPARATOR
                    + "installation by setting the JAVA_HOME environment variable.");
        }
    }

    private static File createArgsFile(List<String> args) throws IOException {
        File file = File.createTempFile(AptUtils.class.getName(), ".argfile");
        file.deleteOnExit();

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(file));

            for (String arg : args) {
                arg = arg.replace(File.separatorChar, '/');

                if (arg.contains(" ")) {
                    arg = "\"" + arg + "\"";
                }

                writer.println(arg);
            }
        } finally {
            writer.close();
        }

        return file;
    }
}
