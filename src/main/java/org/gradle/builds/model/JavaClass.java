package org.gradle.builds.model;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class JavaClass {
    private final String name;
    private final Set<JavaClass> referencedClasses = new LinkedHashSet<>();
    private final Set<String> fieldReferences = new LinkedHashSet<>();
    private final Set<ClassRole> roles = new HashSet<>();

    public JavaClass(String name) {
        this.name = name;
    }

    public String getPackage() {
        int pos = name.lastIndexOf(".");
        return name.substring(0, pos);
    }

    public String getSimpleName() {
        int pos = name.lastIndexOf(".");
        return name.substring(pos+1);
    }

    public String getName() {
        return name;
    }

    public Set<JavaClass> getReferencedClasses() {
        return referencedClasses;
    }

    public void uses(JavaClass javaClass) {
        referencedClasses.add(javaClass);
    }

    public <T extends ClassRole> T role(Class<T> type) {
        for (ClassRole role : roles) {
            if (type.isInstance(role)) {
                return type.cast(role);
            }
        }
        return null;
    }

    public void addRole(ClassRole role) {
        roles.add(role);
    }

    public Set<String> getFieldReferences() {
        return fieldReferences;
    }

    public void addFieldReference(String fullyQualifiedName) {
        fieldReferences.add(fullyQualifiedName);
    }
}
