package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.List;

public abstract class HasSource<T, L> implements Component {
    private final List<Dependency<L>> referencedLibraries = new ArrayList<>();
    private final List<T> sourceFiles = new ArrayList<>();
    private final List<T> testFiles = new ArrayList<>();

    public List<Dependency<L>> getReferencedLibraries() {
        return referencedLibraries;
    }

    public void uses(Dependency<L> library) {
        referencedLibraries.add(library);
    }

    public List<T> getSourceFiles() {
        return sourceFiles;
    }

    public T addSourceFile(T sourceFile) {
        sourceFiles.add(sourceFile);
        return sourceFile;
    }

    public List<T> getTestFiles() {
        return testFiles;
    }

    public T addTestFile(T testFile) {
        testFiles.add(testFile);
        return testFile;
    }
}
