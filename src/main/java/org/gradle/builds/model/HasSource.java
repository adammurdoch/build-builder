package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class HasSource<T> implements Component {
    private final Set<T> sourceFiles = new LinkedHashSet<>();

    public Set<T> getSourceFiles() {
        return sourceFiles;
    }

    public void addSourceFile(T sourceFile) {
        sourceFiles.add(sourceFile);
    }
}
