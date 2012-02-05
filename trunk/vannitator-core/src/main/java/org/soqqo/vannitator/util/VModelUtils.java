package org.soqqo.vannitator.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.soqqo.vannitator.vmodel.VAnnotatedType;
import org.soqqo.vannitator.vmodel.VObjectType;

public class VModelUtils {

    public Set<String> packageListAsNew(Set<VObjectType> types) {

        HashSet<String> packages = new HashSet<String>();
        
        // the objects in the set
        for (VObjectType vtype : types) {
            packages.add(vtype.getPackageName().getAsNew());
        }
        
        return packages;
    }
    

}
