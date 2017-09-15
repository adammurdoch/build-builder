package org.gradle.builds.model;

public abstract class PublishedJvmLibrary extends PublishedLibrary {
    protected PublishedJvmLibrary(ExternalDependencyDeclaration gav) {
        super(gav);
    }

    public abstract JvmLibraryApi getApi();
}
