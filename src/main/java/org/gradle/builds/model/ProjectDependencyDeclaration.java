package org.gradle.builds.model;

public class ProjectDependencyDeclaration extends DependencyDeclaration {
    private final String projectPath;

    public ProjectDependencyDeclaration(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getProjectPath() {
        return projectPath;
    }
}
