package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildSettingsBuilder;
import org.gradle.builds.model.BuildTreeBuilder;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.PublicationTarget;

public class SourceDependencyBuildAssembler implements BuildTreeAssembler {
    private final ProjectInitializer initializer;
    private final int sourceDependencyBuilds;

    public SourceDependencyBuildAssembler(ProjectInitializer initializer, int sourceDependencyBuilds) {
        this.initializer = new EmptyRootProjectInitializer(initializer);
        this.sourceDependencyBuilds = sourceDependencyBuilds;
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        if (sourceDependencyBuilds > 0) {
            Graph graph = new GraphAssembler().arrange(sourceDependencyBuilds + 1);
            graph.visit((Graph.Visitor<BuildSettingsBuilder>) (node, dependencies) -> {
                BuildSettingsBuilder build;
                if (node.getLayer() == 0) {
                    build = model.getMainBuild();
                } else {
                    String name = "src" + node.getNameSuffix();
                    String typeName = "Src" + node.getNameSuffix();
                    BuildSettingsBuilder childBuild = model.addBuild(model.getRootDir().resolve("external/source" + node.getNameSuffix()));
                    childBuild.setDisplayName("source dependency build " + name);
                    childBuild.setRootProjectName(name);
                    childBuild.setSettings(new Settings(3, settings.getSourceFileCount()));
                    childBuild.setProjectInitializer(initializer);
                    childBuild.setTypeNamePrefix(typeName);
                    childBuild.publishAs(new PublicationTarget(null));
                    build = childBuild;
                }
                for (Dependency<BuildSettingsBuilder> childBuild : dependencies) {
                    build.sourceDependency(childBuild.getTarget());
                    build.dependsOn(childBuild.getTarget());
                }
                return build;
            });
        }
    }
}
