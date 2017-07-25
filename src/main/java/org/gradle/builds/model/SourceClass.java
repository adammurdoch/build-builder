package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SourceClass<T> {
    private final String name;
    private final Set<T> referencedClasses = new LinkedHashSet<>();

    protected SourceClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<T> getReferencedClasses() {
        return referencedClasses;
    }

    public void uses(T refClass) {
        referencedClasses.add(refClass);
    }

}
