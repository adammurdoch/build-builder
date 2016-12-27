package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class CppClass {
    private final String name;
    private final Set<CppClass> referencedClasses = new LinkedHashSet<>();

    public CppClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<CppClass> getReferencedClasses() {
        return referencedClasses;
    }

    public void uses(CppClass cppClass) {
        referencedClasses.add(cppClass);
    }
}
