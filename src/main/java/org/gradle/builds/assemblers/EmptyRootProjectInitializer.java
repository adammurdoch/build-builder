package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

class EmptyRootProjectInitializer extends ProjectInitializer {
    private final ProjectInitializer initializer;

    public EmptyRootProjectInitializer(ProjectInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public void initRootProject(Project project) {
        // Nothing
    }

    @Override
    public void initLibraryProject(Project project) {
        initializer.initLibraryProject(project);
    }

    @Override
    public void initAlternateLibraryProject(Project project) {
        initializer.initAlternateLibraryProject(project);
    }

    @Override
    public void dependsOn(Project project, Project dependency) {
        if (project.getParent() == null) {
            // Ignore
            return;
        }
        initializer.dependsOn(project, dependency);
    }
}
