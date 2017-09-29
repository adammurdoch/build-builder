package org.gradle.builds.model;

public class HasSwiftSource extends HasSource<SwiftSourceFile, SwiftLibraryApi> {
    private final boolean swiftPm;
    private String module;

    public HasSwiftSource(boolean swiftPm) {
        this.swiftPm = swiftPm;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public boolean isSwiftPm() {
        return swiftPm;
    }

    public SwiftSourceFile addSourceFile(String name) {
        SwiftSourceFile sourceFile = new SwiftSourceFile(name);
        addSourceFile(sourceFile);
        return sourceFile;
    }
}
