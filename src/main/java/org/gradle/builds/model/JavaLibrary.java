package org.gradle.builds.model;

import java.util.Collections;

public class JavaLibrary extends HasJavaSource<JavaLibraryApi> implements JvmLibrary {
    private final String projectName;
    private String targetJavaVersion;
    private JavaClass apiClass;

    public JavaLibrary(Project project) {
        this.projectName = project.getName();
        apiClass = addClass(project.getQualifiedNamespaceFor() + "." + project.getTypeNameFor());
    }

    public String getTargetJavaVersion() {
        return targetJavaVersion;
    }

    public void setTargetJavaVersion(String version) {
        this.targetJavaVersion = version;
    }

    public JavaClass getApiClass() {
        return apiClass;
    }

    @Override
    public JavaLibraryApi getApi() {
        return new JavaLibraryApi(projectName, Collections.singletonList(apiClass.getApi()));
    }
}
