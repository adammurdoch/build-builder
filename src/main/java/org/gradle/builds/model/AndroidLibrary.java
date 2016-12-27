package org.gradle.builds.model;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private JavaClass apiClass;

    public JavaClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
