package org.gradle.builds.model;

public class PublishedAndroidLibrary extends PublishedJvmLibrary {
    private final AndroidLibraryApi api;

    public PublishedAndroidLibrary(ExternalDependencyDeclaration gav, AndroidLibraryApi api) {
        super(gav);
        this.api = api;
    }

    @Override
    public AndroidLibraryApi getApi() {
        return api;
    }
}
