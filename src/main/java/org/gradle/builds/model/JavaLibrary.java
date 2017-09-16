package org.gradle.builds.model;

import java.util.Collections;

public class JavaLibrary extends HasJavaSource<JvmLibraryApi> implements JvmLibrary {
    private final String projectName;
    private String targetJavaVersion;
    private JavaClass apiClass;

    public JavaLibrary(String projectName) {
        this.projectName = projectName;
    }

    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String version) {
        this.targetJavaVersion = version;
    }

    @Override
    public JavaLibraryApi getApi() {
        return new JavaLibraryApi(projectName, Collections.singletonList(apiClass.getApi()));
    }

    public void setApiClass(JavaClass apiClass) {
        this.apiClass = apiClass;
    }
}
