package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

public class InitialProjectSetupBuildConfigurer implements BuildConfigurer {
    private final BuildConfigurer configurer;
    private final GraphAssembler graphAssembler;

    public InitialProjectSetupBuildConfigurer(BuildConfigurer configurer, GraphAssembler graphAssembler) {
        this.configurer = configurer;
        this.graphAssembler = graphAssembler;
    }

    @Override
    public void populate(Build build) {
        StructureAssembler structureAssembler = new StructureAssembler(graphAssembler);
        structureAssembler.arrangeProjects(build, build.getProjectInitializer());
        structureAssembler.arrangeClasses(build);

        // Define publications
        for (Project project: build.getProjects()) {
            project.setVersion(build.getVersion());
        }
        if (build.getPublicationTarget() != null) {
            for (Project project: build.getProjects()) {
                project.publishAs(build.getPublicationTarget());
            }
        }

        // Add incoming dependencies
        for (Build other: build.getDependsOn()) {
            if (other.getPublicationTarget().getHttpRepository() != null) {
                build.getRootProject().getBuildScript().allProjects().maven(other.getPublicationTarget().getHttpRepository());
            }
            for (Project project: build.getProjects()) {
                project.requires(other.getExportedLibraries());
            }
        }

        configurer.populate(build);
    }
}
