package org.gradle.builds.model;

public class ExternalDependencyDeclaration extends DependencyDeclaration {
    private final String gav;

    public ExternalDependencyDeclaration(String gav) {
        this.gav = gav;
    }

    public ExternalDependencyDeclaration(String group, String module, String version) {
        this.gav = group + ':' + module + ':' + version;
    }

    public String getGav() {
        return gav;
    }
}
