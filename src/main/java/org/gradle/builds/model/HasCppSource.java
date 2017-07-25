package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasCppSource extends HasSource<CppSourceFile> {
    private final Set<CppHeaderFile> implHeaders = new LinkedHashSet<>();
    private final Set<CppHeaderFile> publicHeaders = new LinkedHashSet<>();

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

    public CppSourceFile addSourceFile(String name) {
        CppSourceFile sourceFile = new CppSourceFile(name);
        addSourceFile(sourceFile);
        return sourceFile;
    }
}
