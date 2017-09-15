package org.gradle.builds.model;

public abstract class PublishedLibrary implements Component {
    private final ExternalDependencyDeclaration gav;

    protected PublishedLibrary(ExternalDependencyDeclaration gav) {
        this.gav = gav;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
