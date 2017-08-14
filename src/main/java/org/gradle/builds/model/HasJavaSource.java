package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HasJavaSource extends HasSource<JavaClass> {
    private final Set<JvmLibraryApi> referencedLibraries = new LinkedHashSet<>();

    public Set<JvmLibraryApi> getReferencedLibraries() {
        return referencedLibraries;
    }

    public void uses(JvmLibraryApi libraryApi) {
        referencedLibraries.add(libraryApi);
    }

    public JavaClass addClass(String name) {
        return addSourceFile(new JavaClass(name));
    }

    public JavaClass addTest(String name) {
        return addTestFile(new JavaClass(name));
    }
}
