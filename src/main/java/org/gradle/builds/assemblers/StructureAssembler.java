package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

public class StructureAssembler {
    public void populate(int projects, Build build) {
        build.getRootProject().setRole(Project.Role.Application);
        for(int i = 1; i < projects; i++) {
            Project project = build.addProject("lib" + i);
            project.setRole(Project.Role.Library);
        }
    }
}
