package org.soqqo.vannitator.vmodel.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.soqqo.vannitator.cache.CacheWrapper;
import org.soqqo.vannitator.cache.StaticHashMapCache;
import org.soqqo.vannitator.processors.ConfigBean;
import org.soqqo.vannitator.support.AnnotationManager;
import org.soqqo.vannitator.util.JavaTypeElementUtil;
import org.soqqo.vannitator.vmodel.VAnnotatedType;
import org.soqqo.vannitator.vmodel.VClassField;
import org.soqqo.vannitator.vmodel.VGenericType;
import org.soqqo.vannitator.vmodel.VMethod;
import org.soqqo.vannitator.vmodel.VMethodParameter;
import org.soqqo.vannitator.vmodel.VNonAnnotatedType;
import org.soqqo.vannitator.vmodel.VObjectType;
import org.soqqo.vannitator.vmodel.VPrimitiveType;
import org.soqqo.vannitator.vmodel.VType;
import org.soqqo.vannitator.vmodel.VValueType;
import org.soqqo.vannitator.vmodel.VVoidType;

public class VModelBuilder<T extends Annotation> {

    private CacheWrapper<String, VAnnotatedType<T>> theCache;

    private final ProcessingEnvironment environment;

    public VModelBuilder(ProcessingEnvironment environment) {
        this.environment = environment;

        /*
         * CacheManager manager = CacheManager.create(); Cache cache = new
         * Cache(new CacheConfiguration(CACHE_NAME,
         * 4000).diskPersistent(true).eternal(true)); manager.addCache(cache);
         * theCache = new EhcacheWrapper<String, VAnnotatedType>(CACHE_NAME,
         * manager);
         */
        theCache = new StaticHashMapCache<String, VAnnotatedType<T>>();
    }

    public void mapToVModel(Class<T> baseVannitationClass, Annotation supportedAnnotation, Element annotatedClass) {

        // we know we are only ever called to process an annotated type
        VAnnotatedType<T> theVType = (VAnnotatedType<T>) generateVType(baseVannitationClass, true, supportedAnnotation.annotationType().getName(), annotatedClass.asType());

        // create a key with the annotation class and the full class name
        theCache.put(toKey(supportedAnnotation.annotationType().getName().toString(), theVType.getQualifiedName().getName()), theVType);

    }

    public VAnnotatedType<T> getVAnnotatedType(Annotation supportedAnnotation, String classFQN) {

        String key = toKey(supportedAnnotation.annotationType().getName().toString(), classFQN);
        VAnnotatedType<T> vtype = theCache.get(key);
        if (vtype == null) {
            // ? FIXME .. I need to load this up.
            // mapToVModel(supportedAnnotation, annotatedClass);

        }
        return theCache.get(key);
    }

    public VAnnotatedType<T> getVAnnotatedType(String annotationFQN, String classFQN) {
        return theCache.get(toKey(annotationFQN, classFQN));
    }

    /**
     * Simpel contatentation of the two items
     * 
     * @param annotatationClass
     * @param annotatedClass
     * @return
     */
    private String toKey(String annotatationClass, String annotatedClass) {
        return annotatationClass + "|" + annotatedClass;
    }

    /**
     * Create a new VType from the TypeMirror
     * 
     * @param theType
     * @param withNewNames
     * @param annotationClassFQN
     * @param helper
     * @return
     */
    private VType generateVType(Class<T> baseVannitationClass, boolean ignoreCache, String annotationClassFQN, TypeMirror theType) {

        VType theVType;
        // it;s a void return
        if (theType.getKind().equals(TypeKind.VOID)) {
            theVType = new VVoidType();
            // it's a primitive
        } else if (theType.getKind().isPrimitive()) {
            String name = environment.getTypeUtils().getPrimitiveType(theType.getKind()).toString();
            theVType = new VPrimitiveType(name);

            // it's a proper class
        } else {

            TypeElement theTypeElement = (TypeElement) environment.getTypeUtils().asElement(theType);
            String classFQN = theTypeElement.getQualifiedName().toString();
            String simpleName = theTypeElement.getSimpleName().toString();
            String packageName = classFQN.subSequence(0, classFQN.length() - simpleName.length() - 1).toString();

            // FIXME This Type needs to be Generic-ised
            ConfigBean<T> cfgBean = AnnotationManager.instance().getConfAnnotation(baseVannitationClass, classFQN, annotationClassFQN);

            // if there is a cfgBean, it means that this class is annotated with
            // one of our annotations
            if (cfgBean != null) {
                theVType = theCache.get(toKey(annotationClassFQN, classFQN));
                if (theVType == null || ignoreCache) {
                    VAnnotatedType<T> ann = new VAnnotatedType<T>(cfgBean, packageName, simpleName);
                    theVType = ann;
                    theCache.put(toKey(annotationClassFQN, classFQN), ann);

                    // methods
                    loadMethods(baseVannitationClass, ann, false, annotationClassFQN, theTypeElement);

                    // fields
                    loadFields(baseVannitationClass, ann, false, annotationClassFQN, theTypeElement);
                }

            } else {

                if (theTypeElement.getTypeParameters().size() == 0) {
                    VNonAnnotatedType non = new VNonAnnotatedType(packageName, simpleName);
                    theVType = non;

                } else {

                    theVType = createAsGenericType(baseVannitationClass, annotationClassFQN, theTypeElement, packageName, simpleName);

                }

            }

        }
        return theVType;
    }

    /**
     * Create a generic type (because it is found to have type parameters)
     * 
     * @param typesUtil
     * @param annotationClassFQN
     * @param theTypeElement
     * @return
     */
    private VGenericType createAsGenericType(Class<T> baseVannitationClass, String annotationClassFQN, TypeElement theTypeElement, String packageName, String simpleName) {

        VGenericType gen = new VGenericType(packageName, simpleName);
        for (TypeParameterElement p : theTypeElement.getTypeParameters()) {

            // look into the cache to see if we have it already
            // TODO fix it for parameter types a Set<Set<Foo>> )
            // currently all Sets will look the same
            //
            TypeElement parameterTypeElement = (TypeElement) environment.getTypeUtils().asElement(p.getGenericElement().asType());
            VType parameterType = theCache.get(toKey(annotationClassFQN, parameterTypeElement.getQualifiedName().toString()));

            if (parameterType != null) {
                parameterType = generateVType(baseVannitationClass, false, annotationClassFQN, parameterTypeElement.asType());
                gen.addParameterType((VObjectType) parameterType);
            }
        }
        return gen;
    }

    private void loadFields(Class<T> baseVannitationClass, VAnnotatedType<T> annotatedType, boolean ignoreCache, String annotationClassFQN, TypeElement theType) {
        ArrayList<VClassField> fields = new ArrayList<VClassField>();
        List<VariableElement> fieldTypes = JavaTypeElementUtil.getFields(environment, theType);
        for (VariableElement field : fieldTypes) {
            // create
            VClassField vfield = new VClassField(field.getSimpleName().toString());
            VValueType fieldType = (VValueType) generateVType(baseVannitationClass, false, annotationClassFQN, field.asType());
            vfield.setType(fieldType);
            fields.add(vfield);

        }

        annotatedType.setFields(fields);
    }

    private void loadMethods(Class<T> baseVannitationClass, VAnnotatedType<T> annotatedType, boolean ignoreCache, String annotationClassFQN, TypeElement theType) {

        ArrayList<VMethod> methods = new ArrayList<VMethod>();
        List<ExecutableElement> methodTypes = JavaTypeElementUtil.getNormalMethods(theType);
        for (ExecutableElement methodType : methodTypes) {

            // create
            VMethod m = new VMethod(methodType.getSimpleName().toString());
            methods.add(m);

            // set the return type
            TypeMirror returnType = methodType.getReturnType();
            VType ourReturnType = generateVType(baseVannitationClass, false, annotationClassFQN, returnType);
            m.setReturnType(ourReturnType);

            // now parameters
            for (VariableElement variable : methodType.getParameters()) {

                VMethodParameter param = new VMethodParameter(variable.getSimpleName().toString());
                VValueType paramType = (VValueType) generateVType(baseVannitationClass, false, annotationClassFQN, variable.asType());
                param.setType(paramType);

                m.addParameter(param);
            }

        }

        annotatedType.setMethods(methods);

    }
}
