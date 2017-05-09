package org.gradle.builds.model;

public class PublishedJvmLibrary implements Component {
    private final ExternalDependencyDeclaration gav;
    private final JavaClassApi apiClass;

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav, JavaClassApi apiClass) {
        this.gav = gav;
        this.apiClass = apiClass;
    }

    public JavaClassApi getApiClass() {
        return apiClass;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
