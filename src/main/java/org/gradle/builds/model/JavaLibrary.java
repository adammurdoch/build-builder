package org.gradle.builds.model;

public class JavaLibrary extends HasJavaSource implements JvmLibrary {
    private String targetJavaVersion;
    private JavaClass apiClass;

    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String targetJavaVersion) {
        this.targetJavaVersion = targetJavaVersion;
    }

    @Override
    public JavaClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
