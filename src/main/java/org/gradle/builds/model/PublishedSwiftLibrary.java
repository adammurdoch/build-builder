package org.gradle.builds.model;

public class PublishedSwiftLibrary extends PublishedLibrary {
    private final SwiftLibraryApi api;

    public PublishedSwiftLibrary(ExternalDependencyDeclaration gav, SwiftLibraryApi api) {
        super(gav);
        this.api = api;
    }

    public SwiftLibraryApi getApi() {
        return api;
    }
}
