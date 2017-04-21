package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractModelAssembler implements ModelAssembler {
    @Override
    public void apply(Class<? extends Component> component, Project project) {
    }

    @Override
    public void populate(Build build) {
        rootProject(build.getRootProject());
        Set<Project> seen = new HashSet<>();
        for (Project project : build.getProjects()) {
            populate(build.getSettings(), project, seen);
        }
    }

    private void populate(Settings settings, Project project, Set<Project> seen) {
        if (!seen.add(project)) {
            return;
        }

        for (Project dep : project.getDependencies()) {
            populate(settings, dep, seen);
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

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
