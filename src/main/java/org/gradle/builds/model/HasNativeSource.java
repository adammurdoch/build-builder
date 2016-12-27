package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasNativeSource implements Component {
    private final Set<CppHeaderFile> headerFiles = new LinkedHashSet<>();
    private final Set<CppSourceFile> sourceFiles = new LinkedHashSet<>();

    public Set<CppHeaderFile> getHeaderFiles() {
        return headerFiles;
    }

    public CppHeaderFile addHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        headerFiles.add(headerFile);
        return headerFile;
    }

    public Set<CppSourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public CppSourceFile addSourceFile(String name) {
        CppSourceFile sourceFile = new CppSourceFile(name);
        sourceFiles.add(sourceFile);
        return sourceFile;
    }
}
