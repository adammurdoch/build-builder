package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildStructureBuilder;
import org.gradle.builds.model.BuildTreeBuilder;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.PublicationTarget;

public class IncludedBuildAssembler implements BuildTreeAssembler {
    private final ProjectInitializer initializer;
    private final int includedBuilds;
    private final GraphAssembler graphAssembler;

    public IncludedBuildAssembler(ProjectInitializer initializer, GraphAssembler graphAssembler, int includedBuilds) {
        this.initializer = new EmptyRootProjectInitializer(initializer);
        this.includedBuilds = includedBuilds;
        this.graphAssembler = graphAssembler;
    }

    @Override
    public void populate(Settings settings, BuildTreeBuilder model) {
        Graph graph = graphAssembler.arrange(includedBuilds + 1);
        graph.visit((Graph.Visitor<BuildStructureBuilder>) (node, dependencies) -> {
            BuildStructureBuilder build;
            if (node.getLayer() == 0) {
                build = model.getMainBuild();
            } else {
                String name = "child" + node.getNameSuffix();
                String typeName = "Child" + node.getNameSuffix();
                BuildStructureBuilder childBuild = model.addBuild(name);
                childBuild.setDisplayName("included build " + name);
                childBuild.setRootProjectName(name);
                childBuild.setSettings(new Settings(3, settings.getSourceFileCount()));
                childBuild.getProjectInitializer().add(initializer);
                childBuild.setTypeNamePrefix(typeName);
                childBuild.publishAs(new PublicationTarget(null));

                model.getMainBuild().includeBuild(childBuild);
                build = childBuild;
            }
            for (Dependency<BuildStructureBuilder> dependency : dependencies) {
                build.dependsOn(dependency.getTarget());
            }
            return build;
        });
    }
}
