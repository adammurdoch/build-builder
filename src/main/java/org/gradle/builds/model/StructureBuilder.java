package org.gradle.builds.model;

public class StructureBuilder {
    public void populate(int projects, Build build) {
        build.getRootProject().setRole(Project.Role.Application);
        for(int i = 1; i < projects; i++) {
            Project project = build.addProject("lib" + i);
            project.setRole(Project.Role.Library);
        }
    }
}
