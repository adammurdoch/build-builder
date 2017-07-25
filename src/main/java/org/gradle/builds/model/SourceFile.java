package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A source file containing zero or more classes.
 *
 * @param <T> the type of classes contained in this source file.
 */
public abstract class SourceFile<T> {
    private final String name;
    private final Set<T> classes = new LinkedHashSet<>();

    protected SourceFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<T> getClasses() {
        return classes;
    }

    public void addClass(T containedClass) {
        classes.add(containedClass);
    }
}
