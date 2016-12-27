package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class CppSourceFile extends CppFile {
    private boolean mainFunction;
    private Set<CppClass> usesClass = new LinkedHashSet<>();

    public CppSourceFile(String name) {
        super(name);
    }

    public boolean hasMainFunction() {
        return mainFunction;
    }

    public Set<CppClass> getMainFunctionReferencedClasses() {
        return usesClass;
    }

    public void addMainFunction(CppClass usesClass) {
        this.usesClass.add(usesClass);
        mainFunction = true;
    }
}
