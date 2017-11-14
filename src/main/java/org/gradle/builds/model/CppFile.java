package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CppFile extends SourceFile<CppClass> {
    private final List<CppHeaderFile> headers = new ArrayList<>();

    protected CppFile(String name) {
        super(name);
    }

    public List<CppHeaderFile> getHeaderFiles() {
        return headers;
    }

    public void includeHeader(CppHeaderFile header) {
        headers.add(header);
    }
}
