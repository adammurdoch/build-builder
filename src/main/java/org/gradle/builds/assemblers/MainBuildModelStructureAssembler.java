package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildTreeBuilder;

public class MainBuildModelStructureAssembler implements BuildTreeAssembler {
    private final ProjectInitializer projectInitializer;

    public MainBuildModelStructureAssembler(ProjectInitializer projectInitializer) {
        this.projectInitializer = projectInitializer;
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        model.getMainBuild().setSettings(settings);
        model.getMainBuild().setProjectInitializer(projectInitializer);
    }
}
