package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasJavaSource implements Component {
    private final Set<JavaClass> sourceFiles = new LinkedHashSet<>();

    public Set<JavaClass> getSourceFiles() {
        return sourceFiles;
    }

    public JavaClass addClass(String name) {
        JavaClass javaClass = new JavaClass(name);
        sourceFiles.add(javaClass);
        return javaClass;
    }
}
