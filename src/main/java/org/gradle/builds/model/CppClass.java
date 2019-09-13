package org.gradle.builds.model;

public class CppClass extends SourceClass<CppClass> {
    public CppClass(String name) {
        super(name);
    }

    @Override
    public CppClass getApi() {
        return this;
    }
}
