package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildSettingsBuilder;
import org.gradle.builds.model.BuildTreeBuilder;
import org.gradle.builds.model.PublicationTarget;

public class IncludedBuildAssembler implements BuildTreeAssembler {
    private final ProjectInitializer initializer;
    private final int builds;

    public IncludedBuildAssembler(ProjectInitializer initializer, int builds) {
        this.initializer = new EmptyRootProjectInitializer(initializer);
        this.builds = builds;
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        for (int i = 1; i <= builds; i++) {
            String name = "child" + i;
            String typeName = "Child" + i;
            BuildSettingsBuilder childBuild = model.addBuild(model.getRootDir().resolve(name));
            childBuild.setDisplayName("included build " + i);
            childBuild.setRootProjectName(name);
            childBuild.setSettings(new Settings(3, settings.getSourceFileCount()));
            childBuild.setProjectInitializer(initializer);
            childBuild.setTypeNamePrefix(typeName);
            childBuild.publishAs(new PublicationTarget(null));

            model.getMainBuild().includeBuild(childBuild);
            model.getMainBuild().dependsOn(childBuild);
        }
    }
}
