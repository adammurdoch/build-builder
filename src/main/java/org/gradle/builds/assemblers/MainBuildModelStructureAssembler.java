package org.gradle.builds.assemblers;

import org.gradle.builds.model.MutableBuildTree;

public class MainBuildModelStructureAssembler implements BuildTreeAssembler {
    private final ProjectInitializer projectInitializer;

    public MainBuildModelStructureAssembler(ProjectInitializer projectInitializer) {
        this.projectInitializer = projectInitializer;
    }

    @Override
    public void attachBuilds(Settings settings, MutableBuildTree model) {
        model.getMainBuild().setSettings(settings);
        model.getMainBuild().setProjectInitializer(projectInitializer);
    }
}
