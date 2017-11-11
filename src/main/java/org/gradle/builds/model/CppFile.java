package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class CppFile extends SourceFile<CppClass> {
    private final Set<CppHeaderFile> headers = new LinkedHashSet<>();

    protected CppFile(String name) {
        super(name);
    }

    public Set<CppHeaderFile> getHeaderFiles() {
        return headers;
    }

    public void includeHeader(CppHeaderFile header) {
        headers.add(header);
    }
}
