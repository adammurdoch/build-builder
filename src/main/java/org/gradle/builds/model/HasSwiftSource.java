package org.gradle.builds.model;

public class HasSwiftSource extends HasSource<SwiftSourceFile, SwiftLibraryApi> {
    private final boolean swiftPm;
    private final String module;

    public HasSwiftSource(boolean swiftPm, String moduleName) {
        this.swiftPm = swiftPm;
        this.module = moduleName;
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
