package org.soqqo.vannitator.support;

import java.lang.annotation.Annotation;

public interface NameHandler<T extends Annotation> {

    public abstract String getNewPackageName(String currentPackageName);

    public abstract String getNewSimpleClassName(String currentClassName);

    public abstract String getNewQualifiedName(String currentClassName, String currentPackageName);

}