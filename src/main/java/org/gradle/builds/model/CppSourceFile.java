package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class CppSourceFile extends CppFile {
    private boolean mainFunction;
    private final Set<CppHeaderFile> headers = new LinkedHashSet<>();

    public CppSourceFile(String name) {
        super(name);
    }

    public boolean hasMainFunction() {
        return mainFunction;
    }

    public void addMainFunction() {
        mainFunction = true;
    }

    public Set<CppHeaderFile> getHeaderFiles() {
        return headers;
    }

    public void addHeader(CppHeaderFile header) {
        headers.add(header);
    }
}
