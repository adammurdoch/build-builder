package org.gradle.builds.model;

public class HasSwiftSource extends HasSource<SwiftSourceFile, SwiftLibraryApi> {
    private final boolean swiftPm;

    public HasSwiftSource(boolean swiftPm) {
        this.swiftPm = swiftPm;
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
