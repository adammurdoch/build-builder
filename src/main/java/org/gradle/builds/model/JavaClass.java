package org.gradle.builds.model;

public class JavaClass {
    private final String name;

    public JavaClass(String name) {
        this.name = name;
    }

    public String getPackage() {
        int pos = name.lastIndexOf(".");
        return name.substring(0, pos);
    }

    public String getSimpleName() {
        int pos = name.lastIndexOf(".");
        return name.substring(pos+1);
    }

    public String getName() {
        return name;
    }
}
