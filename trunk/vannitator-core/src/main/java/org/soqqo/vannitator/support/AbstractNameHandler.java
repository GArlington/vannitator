package org.soqqo.vannitator.support;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractNameHandler {

    public static String utilGetNewPackageName(String packageRename, String currentPackage) {
        if ("".equals(packageRename) || packageRename == null || currentPackage.equals("") || currentPackage == null) {
            return "";
        } else if ("{0-}".equals(packageRename)) {
            return currentPackage;
        } else {
            StringBuffer newPackageName = new StringBuffer();
            String[] packageParts = currentPackage.split("\\.");
            String[] renameParts = packageRename.split("\\.");
    
            // shortcut
            if (packageRename.equals("{0-" + packageParts.length + "}")) {
                return currentPackage;
            }
    
            Pattern p = Pattern.compile("\\{(.*?)\\}");
            for (int j = 0; j < renameParts.length; j++) {
                Matcher m = p.matcher(renameParts[j]);
                if (m.find()) {
                    String rangeIdentifier = renameParts[j].substring(m.start(1), m.end(1));
                    newPackageName.append(getPackageParts(packageParts, rangeIdentifier));
                } else {
                    newPackageName.append(renameParts[j]);
                }
                if (j < renameParts.length - 1) {
                    newPackageName.append(".");
                }
            }
            return newPackageName.toString();
        }
    
    }

    private static String getPackageParts(String[] packageParts, String rangeIdentifier) {
    
        int lowerBound;
        int upperBound;
        String[] ranges = rangeIdentifier.split("-");
        if (ranges.length == 0) {
            return "";
        }
    
        if ("".equals(ranges[0])) {
            lowerBound = 0;
        } else {
            lowerBound = Integer.parseInt(ranges[0]);
        }
    
        if (ranges.length > 1) {
            if ("".equals(ranges[1])) {
                upperBound = packageParts.length - 1;
            } else {
                upperBound = Integer.parseInt(ranges[1]);
            }
        } else {
            upperBound = lowerBound;
        }
    
        // check them
        if (upperBound > packageParts.length - 1) {
            upperBound = packageParts.length - 1;
        }
    
        // check
        if (lowerBound < 0) {
            lowerBound = 0;
        }
    
        if (lowerBound > packageParts.length - 1) {
            lowerBound = packageParts.length - 1;
        }
    
        StringBuffer b = new StringBuffer();
        for (int i = lowerBound; i <= upperBound; i++) {
            b.append(packageParts[i]);
            if (i != upperBound) {
                b.append(".");
            }
        }
        return b.toString();
    }

    public AbstractNameHandler() {
        super();
    }

}