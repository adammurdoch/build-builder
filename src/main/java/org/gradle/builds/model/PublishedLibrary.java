package org.gradle.builds.model;

public class PublishedLibrary<T> implements Library<T> {
    private final String displayName;
    private final ExternalDependencyDeclaration gav;
    private final T api;

    public PublishedLibrary(String displayName, ExternalDependencyDeclaration gav, T api) {
        this.displayName = displayName;
        this.gav = gav;
        this.api = api;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ExternalDependencyDeclaration getDependency() {
        return gav;
    }

    @Override
    public T getApi() {
        return api;
    }
}
