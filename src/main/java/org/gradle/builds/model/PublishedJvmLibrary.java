package org.gradle.builds.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PublishedJvmLibrary implements Component {
    private final ExternalDependencyDeclaration gav;
    private final Set<JavaClassApi> apiClasses;

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav, JavaClassApi apiClass) {
        this.gav = gav;
        this.apiClasses = Collections.singleton(apiClass);
    }

    public PublishedJvmLibrary(ExternalDependencyDeclaration gav, Collection<JavaClassApi> apiClasses) {
        this.gav = gav;
        this.apiClasses = new LinkedHashSet<>(apiClasses);
    }

    public Set<JavaClassApi> getApi() {
        return apiClasses;
    }

    public ExternalDependencyDeclaration getGav() {
        return gav;
    }
}
