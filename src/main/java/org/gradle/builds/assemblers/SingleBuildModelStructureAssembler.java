package org.gradle.builds.assemblers;

import org.gradle.builds.model.Model;

public class SingleBuildModelStructureAssembler implements ModelStructureAssembler {
    @Override
    public void attachBuilds(Settings settings, Model model) {
        model.getBuild().setSettings(settings);
    }
}
