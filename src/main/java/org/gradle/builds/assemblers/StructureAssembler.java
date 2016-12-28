package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

public class StructureAssembler {
    public void populate(Settings settings, Build build) {
        int projects = settings.getProjectCount();
        build.getRootProject().setRole(Project.Role.Application);
        if (projects == 1) {
            return;
        }

        Project core = build.addProject("core");
        core.setRole(Project.Role.Library);
        if (projects == 2) {
            build.getRootProject().dependsOn(core);
            return;
        }

        for (int i = 1; i < projects - 1; i++) {
            Project project = build.addProject("lib" + i);
            project.setRole(Project.Role.Library);
            build.getRootProject().dependsOn(project);
            project.dependsOn(core);
        }
    }
}
