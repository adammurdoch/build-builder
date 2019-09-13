package org.gradle.builds.model;

public class LocalLibrary<T extends LibraryApi> implements Library<T> {
    private final String project;
    private final T api;
    private final ExternalDependencyDeclaration gav;
    private final String name;

    public LocalLibrary(Project project, ExternalDependencyDeclaration gav, T api) {
        this.project = project.getPath();
        name = project.getName();
        this.api = api;
        this.gav = gav;
    }

    @Override
    public String getDisplayName() {
        return name;
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
            return new PublishedLibrary<>(name, gav, api);
        }
        return null;
    }
}
