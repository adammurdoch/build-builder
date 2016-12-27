package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.util.HashSet;
import java.util.Set;

public abstract class ModelAssembler {
    public void populate(Build build) {
        rootProject(build.getRootProject());
        Set<Project> seen = new HashSet<>();
        for (Project project : build.getProjects()) {
            populate(project, seen);
        }
    }

    private void populate(Project project, Set<Project> seen) {
        if (!seen.add(project)) {
            return;
        }

        for (Project dep : project.getDependencies()) {
            populate(dep, seen);
        }

        populate(project);
    }

    /**
     * Called after dependencies have been populated.
     */
    protected abstract void populate(Project project);

    protected void rootProject(Project rootProject) {
    }

    /**
     * Returns an identifier for the project, can be used in Java package names.
     */
    protected String javaIdentifierFor(Project project) {
        if (project.getParent() == null) {
            return "org.gradle.example";
        }
        return "org.gradle.example." + project.getName();
    }
}
