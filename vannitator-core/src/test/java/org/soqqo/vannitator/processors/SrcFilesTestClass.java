package org.soqqo.vannitator.processors;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.DirectoryScanner;

public class SrcFilesTestClass {

    public SrcFilesTestClass(String srcBasePath) {
        this.srcBasePath = srcBasePath;
        init();
    }

    public SrcFilesTestClass() {
        init();
    }

    private final Set<String> sourceFiles = new HashSet<String>();

    private String getSrcBasePath() {
        return srcBasePath;
    }

    private String srcBasePath = "src/test/java";

    private void scanForSourceFiles(DirectoryScanner scanner) {
        if (scanner.getIncludedFilesCount() > 0) {
            String[] files = scanner.getIncludedFiles();
            for (String file : files) {
                sourceFiles.add(new File(getSrcBasePath(), file).getAbsolutePath());
            }
        }
    }

    private void init() {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(getSrcBasePath());
        ds.setIncludes(new String[] { "**/*.java" });
        ds.scan();
        scanForSourceFiles(ds);
    }

    public String[] srcFiles() {
        return (String[]) sourceFiles.toArray(new String[]{});
    }

}
