package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

/**
 * Responsible for setting the initial state for a project.
 */
public abstract class ProjectInitializer {
    public abstract void initRootProject(Project project);

    public abstract void initLibraryProject(Project project);

    public void initAlternateLibraryProject(Project project) {
        initLibraryProject(project);
    }
}
