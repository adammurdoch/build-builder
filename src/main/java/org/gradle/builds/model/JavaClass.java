package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class JavaClass {
    private final String name;
    private final Set<JavaClass> referencedClasses = new LinkedHashSet<>();
    private boolean mainMethod;

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

    public boolean hasMainMethod() {
        return mainMethod;
    }

    public void addMainMethod() {
        mainMethod = true;
    }
}
