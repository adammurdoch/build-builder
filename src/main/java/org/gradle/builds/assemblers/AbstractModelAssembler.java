package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

public abstract class AbstractModelAssembler implements ProjectConfigurer {
    @Override
    public void configure(Settings settings, Project project) {
        if (project.getParent() == null) {
            rootProject(project);
        }
        populate(settings, project);
    }

    /**
     * Called after dependencies have been populated.
     */
    protected abstract void populate(Settings settings, Project project);

    protected void rootProject(Project rootProject) {
    }

    /**
     * Returns an identifier for the project, can be used in Java package names.
     */
    protected String javaPackageFor(Project project) {
        if (project.getParent() == null) {
            return "org.gradle.example";
        }
        return "org.gradle.example." + project.getName();
    }

    /**
     * Returns an identifier for the project, can be used as a class name.
     */
    protected String classNameFor(Project project) {
        if (project.getParent() == null) {
            return "App";
        }
        return capitalize(project.getName());
    }

    /**
     * Returns an identifier for the project, can be used as a file name.
     */
    protected String fileNameFor(Project project) {
        if (project.getParent() == null) {
            return "app";
        }
        return project.getName().toLowerCase();
    }

    protected String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
