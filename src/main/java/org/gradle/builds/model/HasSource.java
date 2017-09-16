package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class HasSource<T, L> implements Component {
    private final Set<L> referencedLibraries = new LinkedHashSet<>();
    private final Set<T> sourceFiles = new LinkedHashSet<>();
    private final Set<T> testFiles = new LinkedHashSet<>();

    public Set<L> getReferencedLibraries() {
        return referencedLibraries;
    }

    public void uses(L library) {
        referencedLibraries.add(library);
    }

    public Set<T> getSourceFiles() {
        return sourceFiles;
    }

    public T addSourceFile(T sourceFile) {
        sourceFiles.add(sourceFile);
        return sourceFile;
    }

    public Set<T> getTestFiles() {
        return testFiles;
    }

    public T addTestFile(T testFile) {
        testFiles.add(testFile);
        return testFile;
    }
}
