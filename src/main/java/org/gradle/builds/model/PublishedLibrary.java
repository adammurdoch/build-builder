package org.gradle.builds.model;

public class PublishedLibrary<T> implements Component {
    private final ExternalDependencyDeclaration gav;
    private final T api;

    public PublishedLibrary(ExternalDependencyDeclaration gav, T api) {
        this.gav = gav;
        this.api = api;
    }

    public T getApi() {
        return api;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
