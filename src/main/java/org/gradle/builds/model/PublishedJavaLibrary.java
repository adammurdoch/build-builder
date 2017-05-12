package org.gradle.builds.model;

import java.util.Collections;

public class PublishedJavaLibrary extends PublishedJvmLibrary {
    private final JavaLibraryApi api;

    public PublishedJavaLibrary(ExternalDependencyDeclaration gav, JavaClassApi apiClass) {
        super(gav);
        this.api = new JavaLibraryApi(Collections.singletonList(apiClass));
    }

    public PublishedJavaLibrary(ExternalDependencyDeclaration gav, JavaLibraryApi api) {
        super(gav);
        this.api = api;
    }

    @Override
    public JavaLibraryApi getApi() {
        return api;
    }
}
