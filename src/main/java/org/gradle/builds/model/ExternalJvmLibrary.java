package org.gradle.builds.model;

public class ExternalJvmLibrary {
    private final ExternalDependencyDeclaration gav;

    public ExternalJvmLibrary(ExternalDependencyDeclaration gav) {
        this.gav = gav;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
