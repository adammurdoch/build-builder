package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;
import org.gradle.builds.model.PublicationTarget;

public class IncludedBuildAssembler implements ModelStructureAssembler {
    private final ProjectInitializer initializer;
    private final int builds;

    public IncludedBuildAssembler(ProjectInitializer initializer, int builds) {
        this.initializer = new EmptyRootProjectInitializer(initializer);
        this.builds = builds;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        for (int i = 1; i < builds; i++) {
            String name = "child" + i;
            Build childBuild = new Build(model.getBuild().getRootDir().resolve(name), name);
            childBuild.setSettings(settings);
            childBuild.setProjectInitializer(initializer);
            childBuild.setTypeNamePrefix("Child" + i);
            childBuild.publishAs(new PublicationTarget(null));
            model.addBuild(childBuild);
            model.getBuild().includeBuild(childBuild);
            model.getBuild().dependsOn(childBuild);
        }
    }
}
