package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CppFile {
    private final String name;
    private final Set<CppClass> classes = new LinkedHashSet<>();

    protected CppFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<CppClass> getClasses() {
        return classes;
    }

    public void addClass(CppClass cppClass) {
        classes.add(cppClass);
    }
}
