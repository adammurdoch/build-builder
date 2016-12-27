package org.gradle.builds.model;

public class CppSourceFile {
    private final String name;

    public CppSourceFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
