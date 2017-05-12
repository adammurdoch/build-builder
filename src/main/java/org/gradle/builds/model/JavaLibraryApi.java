package org.gradle.builds.model;

import java.util.List;

public class JavaLibraryApi implements JvmLibraryApi {
    private final String projectName;
    private final List<JavaClassApi> apiClasses;

    public JavaLibraryApi(String projectName, List<JavaClassApi> apiClasses) {
        this.projectName = projectName;
        this.apiClasses = apiClasses;
    }

    @Override
    public String getIdentifier() {
        return projectName;
    }

    @Override
    public String getDisplayName() {
        return "project " + projectName;
    }

    @Override
    public List<JavaClassApi> getApiClasses() {
        return apiClasses;
    }
}
