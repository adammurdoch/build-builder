package org.gradle.builds.model;

public class JavaLibrary extends HasJavaSource implements JvmLibrary {
    private JavaClass apiClass;

    @Override
    public JavaClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
