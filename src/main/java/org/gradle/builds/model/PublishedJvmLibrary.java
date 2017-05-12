package org.gradle.builds.model;

public abstract class PublishedJvmLibrary implements Component {
    private final ExternalDependencyDeclaration gav;

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav) {
        this.gav = gav;
    }

    public abstract JvmLibraryApi getApi();

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
