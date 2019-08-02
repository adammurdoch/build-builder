package org.gradle.builds.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class JavaClass extends JvmClass<JavaClassApi> {
    private final Set<String> fieldReferences = new LinkedHashSet<>();

    public JavaClass(String name) {
        super(name);
    }

    public JavaClassApi getApi() {
        return new JavaClassApi(getName(), Collections.singleton("getSomeValue()"), Collections.singleton("INT_CONST"));
    }

    public Set<String> getMethodReferences() {
        Set<String> methodReferences = new LinkedHashSet<>();
        for (Dependency<JavaClassApi> dependency : getReferencedClasses()) {
            JavaClassApi javaClass = dependency.getTarget();
            for (String methodName : javaClass.getMethods()) {
                methodReferences.add(javaClass.getName() + "." + methodName);
            }
        }
        return methodReferences;
    }

    public Set<String> getFieldReferences() {
        Set<String> fieldReferences = new LinkedHashSet<>();
        for (Dependency<JavaClassApi> dependency : getReferencedClasses()) {
            JavaClassApi javaClass = dependency.getTarget();
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
