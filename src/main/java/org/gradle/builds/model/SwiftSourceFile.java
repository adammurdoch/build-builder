package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class SwiftSourceFile extends SourceFile<SwiftClass> {
    private final Set<String> modules = new LinkedHashSet<>();
    private final Set<SwiftClass> mainClasses = new LinkedHashSet<>();

    public SwiftSourceFile(String name) {
        super(name);
    }

    public Set<String> getModules() {
        return modules;
    }

    public void addModule(String module) {
        this.modules.add(module);
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
