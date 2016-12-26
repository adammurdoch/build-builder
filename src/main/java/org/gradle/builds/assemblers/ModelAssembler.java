package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

public abstract class ModelAssembler {
    public abstract void populate(Build build);

    /**
     * Returns an identifier for the project, can be used in Java package names.
     */
    protected String identifierFor(Project project) {
        if (project.getParent() == null) {
            return "org.gradle.example";
        }
        return "org.gradle.example." + project.getName();
    }
}
