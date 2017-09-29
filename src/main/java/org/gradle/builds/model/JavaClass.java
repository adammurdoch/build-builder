package org.gradle.builds.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class JavaClass extends SourceClass<JavaClassApi> {
    private final Set<String> fieldReferences = new LinkedHashSet<>();

    public JavaClass(String name) {
        super(name);
    }

    public String getPackage() {
        String name = getName();
        int pos = name.lastIndexOf(".");
        return name.substring(0, pos);
    }

    public String getSimpleName() {
        String name = getName();
        int pos = name.lastIndexOf(".");
        return name.substring(pos+1);
    }

    public JavaClassApi getApi() {
        return new JavaClassApi(getName(), Collections.singleton("getSomeValue()"), Collections.singleton("INT_CONST"));
    }

    public void uses(JavaClass javaClass) {
        uses(javaClass.getApi());
    }

    public void uses(Iterable<JavaClassApi> javaClasses) {
        for (JavaClassApi javaClass : javaClasses) {
            uses(javaClass);
        }
    }

    public Set<String> getMethodReferences() {
        Set<String> methodReferences = new LinkedHashSet<>();
        for (JavaClassApi javaClass : getReferencedClasses()) {
            for (String methodName : javaClass.getMethods()) {
                methodReferences.add(javaClass.getName() + "." + methodName);
            }
        }
        return methodReferences;
    }

    public Set<String> getFieldReferences() {
        Set<String> fieldReferences = new LinkedHashSet<>();
        for (JavaClassApi javaClass : getReferencedClasses()) {
            for (String fieldName : javaClass.getFields()) {
                fieldReferences.add(javaClass.getName() + "." + fieldName);
            }
        }
        fieldReferences.addAll(this.fieldReferences);
        return fieldReferences;
    }

    public void addFieldReference(String fullyQualifiedName) {
        fieldReferences.add(fullyQualifiedName);
    }
}
