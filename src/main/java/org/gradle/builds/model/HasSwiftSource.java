package org.gradle.builds.model;

public class HasSwiftSource extends HasSource<SwiftSourceFile> {
    public SwiftSourceFile addSourceFile(String name) {
        SwiftSourceFile sourceFile = new SwiftSourceFile(name);
        addSourceFile(sourceFile);
        return sourceFile;
    }
}
