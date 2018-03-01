package org.gradle.builds.assemblers;

import org.gradle.builds.model.BlockWithProjectTarget;
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

    protected void addIdePlugins(Project rootProject) {
        BlockWithProjectTarget allProjects = rootProject.getBuildScript().allProjects();
        allProjects.requirePlugin("idea");
        allProjects.requirePlugin("eclipse");
        allProjects.requirePlugin("xcode", "4.2");
        allProjects.requirePlugin("visual-studio");
    }

    protected String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
