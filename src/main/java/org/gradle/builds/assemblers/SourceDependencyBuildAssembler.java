package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildStructureBuilder;
import org.gradle.builds.model.BuildTreeBuilder;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.PublicationTarget;

public class SourceDependencyBuildAssembler implements BuildTreeAssembler {
    private final ProjectInitializer initializer;
    private final int sourceDependencyBuilds;
    private final GraphAssembler graphAssembler;

    public SourceDependencyBuildAssembler(ProjectInitializer initializer, GraphAssembler graphAssembler, int sourceDependencyBuilds) {
        this.initializer = new EmptyRootProjectInitializer(initializer);
        this.sourceDependencyBuilds = sourceDependencyBuilds;
        this.graphAssembler = graphAssembler;
    }

    @Override
    public void populate(Settings settings, BuildTreeBuilder model) {
        Graph graph = graphAssembler.arrange(sourceDependencyBuilds + 1);
        graph.visit((Graph.Visitor<BuildStructureBuilder>) (node, dependencies) -> {
            BuildStructureBuilder build;
            if (node.getLayer() == 0) {
                build = model.getMainBuild();
            } else {
                String name = "src" + node.getNameSuffix();
                String typeName = "Src" + node.getNameSuffix();
                BuildStructureBuilder childBuild = model.addBuild("external/source" + node.getNameSuffix());
                childBuild.setDisplayName("source dependency build " + name);
                childBuild.setRootProjectName(name);
                childBuild.setSettings(new Settings(3, settings.getSourceFileCount()));
                childBuild.getProjectInitializer().add(initializer);
                childBuild.setTypeNamePrefix(typeName);
                childBuild.publishAs(new PublicationTarget(null));
                GitRepoBuilder childRepo = model.addRepo(childBuild.getRootDir());
                childRepo.setVersion(childBuild.getVersion());
                build = childBuild;
            }
            for (Dependency<BuildStructureBuilder> childBuild : dependencies) {
                build.sourceDependency(childBuild.getTarget());
                build.dependsOn(childBuild.getTarget());
            }
            return build;
        });
    }
}
