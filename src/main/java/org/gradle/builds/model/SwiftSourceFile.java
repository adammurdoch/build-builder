package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class SwiftSourceFile extends SourceFile<SwiftClass> {
    private Set<SwiftClass> mainClasses = new LinkedHashSet<>();

    public SwiftSourceFile(String name) {
        super(name);
    }

    public boolean hasMainFunction() {
        return !mainClasses.isEmpty();
    }

    public Set<SwiftClass> getMainFunctionReferencedClasses() {
        return mainClasses;
    }

    public void addMainFunction(SwiftClass usesClass) {
        this.mainClasses.add(usesClass);
    }
}
