package org.gradle.builds.model;

import java.util.List;

public class JavaLibraryApi implements JvmLibraryApi {
    private final List<JavaClassApi> apiClasses;

    public JavaLibraryApi(List<JavaClassApi> apiClasses) {
        this.apiClasses = apiClasses;
    }

    @Override
    public List<JavaClassApi> getApiClasses() {
        return apiClasses;
    }
}
