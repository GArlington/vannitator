package org.soqqo.vannitator.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Some basic helpers to determing Java TypeElement
 * 
 * @author rbuckland
 * 
 */
public class JavaTypeElementUtil {

    public static boolean isConstructor(ExecutableElement element) {
        return element.getSimpleName().contentEquals("<init>");
    }

    public static boolean isStaticInit(ExecutableElement element) {
        return element.getSimpleName().contentEquals("<clinit>") && element.getModifiers().contains(Modifier.STATIC);
    }

    public static boolean isInstanceInit(ExecutableElement element) {
        return element.getSimpleName().contentEquals("<clinit>") && !element.getModifiers().contains(Modifier.STATIC);
    }

    public static boolean isNormalMethod(ExecutableElement element) {
        return !element.getSimpleName().toString().contains("<");
    }

    public static List<ExecutableElement> getNormalMethods(TypeElement element) {
        ArrayList<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement instanceof ExecutableElement && isNormalMethod((ExecutableElement) enclosedElement)) {
                methods.add((ExecutableElement) enclosedElement);
            }
        }
        return methods;
    }

    public static boolean isField(Element element) {
        return (element instanceof VariableElement);
    }

    public static List<VariableElement> getFields(ProcessingEnvironment env, TypeElement classRepresenter) {
        List<? extends Element> members = env.getElementUtils().getAllMembers(classRepresenter);
        return ElementFilter.fieldsIn(members);
    }

}
