package org.gradle.builds.model;

public class PublishedJvmLibrary implements Component {
    private final ExternalDependencyDeclaration gav;
    private final JavaClass apiClass;

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav, JavaClass apiClass) {
        this.gav = gav;
        this.apiClass = apiClass;
    }

    public JavaClass getApiClass() {
        return apiClass;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
