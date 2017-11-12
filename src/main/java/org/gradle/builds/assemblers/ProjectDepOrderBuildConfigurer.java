package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.Project;

import java.util.HashSet;
import java.util.Set;

public class ProjectDepOrderBuildConfigurer implements BuildConfigurer {
    private ProjectConfigurer configurer;

    public ProjectDepOrderBuildConfigurer(ProjectConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public void populate(Build build) {
        Set<Project> seen = new HashSet<>();
        for (Project project : build.getProjects()) {
            populate(build.getSettings(), project, seen);
        }
    }

    private void populate(Settings settings, Project project, Set<Project> seen) {
        if (!seen.add(project)) {
            return;
        }

        for (Dependency<Project> dep : project.getDependencies()) {
            populate(settings, dep.getTarget(), seen);
        }

        configurer.configure(settings, project);
    }
}
