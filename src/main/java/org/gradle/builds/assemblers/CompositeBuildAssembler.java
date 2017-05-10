package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;
import org.gradle.builds.model.PublicationTarget;

public class CompositeBuildAssembler implements ModelStructureAssembler {
    private final ModelStructureAssembler structureAssembler;
    private final int builds;

    public CompositeBuildAssembler(ModelStructureAssembler structureAssembler, int builds) {
        this.structureAssembler = structureAssembler;
        this.builds = builds;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        for (int i = 1; i < builds; i++) {
            String name = "child" + i;
            Build childBuild = new Build(model.getBuild().getRootDir().resolve(name), name);
            childBuild.setSettings(settings);
            childBuild.setRootProjectType(null);
            childBuild.setProjectNamePrefix(name + "_");
            childBuild.publishAs(new PublicationTarget(null));
            model.addBuild(childBuild);
            model.getBuild().includeBuild(childBuild);
            model.getBuild().dependsOn(childBuild);
        }
        structureAssembler.attachBuilds(settings, model);
    }
}
