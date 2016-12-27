package org.gradle.builds.model;

public class CppHeaderFile {
    private final String name;

    public CppHeaderFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
