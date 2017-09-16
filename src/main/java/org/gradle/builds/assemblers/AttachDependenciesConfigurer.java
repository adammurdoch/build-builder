package org.gradle.builds.assemblers;

import org.gradle.builds.model.LocalLibrary;
import org.gradle.builds.model.Project;

public class AttachDependenciesConfigurer implements ProjectConfigurer {
    @Override
    public void configure(Settings settings, Project project) {
        for (Project dep : project.getDependencies()) {
            for (LocalLibrary<?> library : dep.getExportedLibraries(Object.class)) {
                project.requires(library);
            }
        }
    }
}
