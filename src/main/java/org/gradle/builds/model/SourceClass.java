package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Some source class that references other classes.
 *
 * @param <REF> The type of classes referenced by this class.
 */
public abstract class SourceClass<REF> {
    private final String name;
    private final Set<REF> referencedClasses = new LinkedHashSet<>();

    protected SourceClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<REF> getReferencedClasses() {
        return referencedClasses;
    }

    public void uses(REF refClass) {
        referencedClasses.add(refClass);
    }

}
