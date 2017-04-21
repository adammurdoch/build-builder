package org.gradle.builds.model;

public class PublishedJvmLibrary implements Component {
    private final ExternalDependencyDeclaration gav;

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav) {
        this.gav = gav;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
