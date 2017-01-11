package org.gradle.builds.model;

public class ExternalDependencyDeclaration extends DependencyDeclaration {
    private final String gav;

    public ExternalDependencyDeclaration(String gav) {
        this.gav = gav;
    }

    public String getGav() {
        return gav;
    }
}
