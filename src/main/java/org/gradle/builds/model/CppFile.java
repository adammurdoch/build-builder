package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CppFile extends SourceFile<CppClass> {
    private final List<CppHeaderFile> headers = new ArrayList<>();
    private final List<String> systemHeaders = new ArrayList<>();

    protected CppFile(String name) {
        super(name);
    }

    public List<CppHeaderFile> getHeaderFiles() {
        return headers;
    }

    public void includeHeader(CppHeaderFile header) {
        headers.add(header);
    }

    public List<String> getSystemHeaders() {
        return systemHeaders;
    }

    public void includeSystemHeader(String header) {
        systemHeaders.add(header);
    }
}
