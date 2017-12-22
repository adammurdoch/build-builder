package org.gradle.builds.assemblers;

import org.gradle.builds.model.Model;

public class MainBuildModelStructureAssembler implements ModelStructureAssembler {
    private final ProjectInitializer projectInitializer;

    public MainBuildModelStructureAssembler(ProjectInitializer projectInitializer) {
        this.projectInitializer = projectInitializer;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        model.getBuild().setSettings(settings);
        model.getBuild().setProjectInitializer(projectInitializer);
    }
}
