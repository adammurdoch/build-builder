package org.gradle.builds.model;

public class HasJavaSource<L extends JvmLibraryApi> extends HasSource<JavaClass, L> {
    public JavaClass addClass(String name) {
        return addSourceFile(new JavaClass(name));
    }

    public JavaClass addTest(String name) {
        return addTestFile(new JavaClass(name));
    }
}
