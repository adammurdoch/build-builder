package org.gradle.builds.model;

public class LocalLibrary<T> implements Library<T> {
    private final String project;
    private final T api;
    private final ExternalDependencyDeclaration gav;

    public LocalLibrary(Project project, ExternalDependencyDeclaration gav, T api) {
        this.project = project.getPath();
        this.api = api;
        this.gav = gav;
    }

    @Override
    public String getDisplayName() {
        return project;
    }

    @Override
    public T getApi() {
        return api;
    }

    @Override
    public ProjectDependencyDeclaration getDependency() {
        return new ProjectDependencyDeclaration(project);
    }

    public PublishedLibrary<T> getPublished() {
        if (gav != null) {
            return new PublishedLibrary<>(project, gav, api);
        }
        return null;
    }
}
