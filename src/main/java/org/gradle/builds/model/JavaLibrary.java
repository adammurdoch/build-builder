package org.gradle.builds.model;

import java.util.Collections;

public class JavaLibrary extends HasJavaSource implements JvmLibrary {
    private String targetJavaVersion;
    private JavaClass apiClass;

    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String version) {
        this.targetJavaVersion = version;
    }

    @Override
    public JavaLibraryApi getApi() {
        return new JavaLibraryApi(Collections.singletonList(apiClass.getApi()));
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
