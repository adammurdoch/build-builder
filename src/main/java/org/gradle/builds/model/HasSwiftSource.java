package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.List;

public class HasSwiftSource extends HasSource<SwiftSourceFile> {
    private final List<SwiftLibraryApi> referencedLibraries = new ArrayList<>();
    private final boolean swiftPm;

    public HasSwiftSource(boolean swiftPm) {
        this.swiftPm = swiftPm;
    }

    public boolean isSwiftPm() {
        return swiftPm;
    }

    public List<SwiftLibraryApi> getReferencedLibraries() {
        return referencedLibraries;
    }

    public void uses(SwiftLibraryApi library) {
        referencedLibraries.add(library);
    }

    public SwiftSourceFile addSourceFile(String name) {
        SwiftSourceFile sourceFile = new SwiftSourceFile(name);
        addSourceFile(sourceFile);
        return sourceFile;
    }
}
