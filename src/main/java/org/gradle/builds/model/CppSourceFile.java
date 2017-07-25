package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class CppSourceFile extends CppFile {
    private Set<CppClass> mainClasses = new LinkedHashSet<>();

    public CppSourceFile(String name) {
        super(name);
    }

    public boolean hasMainFunction() {
        return !mainClasses.isEmpty();
    }

    public Set<CppClass> getMainFunctionReferencedClasses() {
        return mainClasses;
    }

    public void addMainFunction(CppClass usesClass) {
        this.mainClasses.add(usesClass);
    }
}
