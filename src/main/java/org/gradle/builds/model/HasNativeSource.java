package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasNativeSource implements Component {
    private final Set<CppHeaderFile> headerFiles = new LinkedHashSet<>();
    private final Set<CppSourceFile> sourceFiles = new LinkedHashSet<>();

    public Set<CppHeaderFile> getHeaderFiles() {
        return headerFiles;
    }

    public void addHeaderFile(String name) {
        headerFiles.add(new CppHeaderFile(name));
    }

    public Set<CppSourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void addSourceFile(String name) {
        sourceFiles.add(new CppSourceFile(name));
    }
}
