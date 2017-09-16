package org.gradle.builds.model;

public interface Library<T> {
    String getDisplayName();

    DependencyDeclaration getDependency();

    T getApi();
}
