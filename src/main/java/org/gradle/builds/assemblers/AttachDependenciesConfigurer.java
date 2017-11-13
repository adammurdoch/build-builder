package org.gradle.builds.assemblers;

import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.LocalLibrary;
import org.gradle.builds.model.Project;

public class AttachDependenciesConfigurer implements ProjectConfigurer {
    @Override
    public void configure(Settings settings, Project project) {
        for (Dependency<Project> dep : project.getRequiredProjects()) {
            for (LocalLibrary<?> library : dep.getTarget().getExportedLibraries(Object.class)) {
                project.requires(dep.withTarget(library));
            }
        }
    }
}
