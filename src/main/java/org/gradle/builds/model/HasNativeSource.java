package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasNativeSource implements Component {
    private final Set<CppHeaderFile> implHeaders = new LinkedHashSet<>();
    private final Set<CppHeaderFile> publicHeaders = new LinkedHashSet<>();
    private final Set<CppSourceFile> sourceFiles = new LinkedHashSet<>();

    public Set<CppHeaderFile> getPublicHeaderFiles() {
        return publicHeaders;
    }

    public CppHeaderFile addPublicHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        publicHeaders.add(headerFile);
        return headerFile;
    }

    public Set<CppHeaderFile> getImplementationHeaderFiles() {
        return implHeaders;
    }

    public CppHeaderFile addImplementationHeaderFile(String name) {
        CppHeaderFile headerFile = new CppHeaderFile(name);
        implHeaders.add(headerFile);
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
