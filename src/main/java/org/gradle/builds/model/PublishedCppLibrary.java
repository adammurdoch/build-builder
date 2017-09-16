package org.gradle.builds.model;

public class PublishedCppLibrary extends PublishedLibrary {
    private final CppLibraryApi api;

    public PublishedCppLibrary(ExternalDependencyDeclaration gav, CppLibraryApi api) {
        super(gav);
        this.api = api;
    }

    public CppLibraryApi getApi() {
        return api;
    }
}
