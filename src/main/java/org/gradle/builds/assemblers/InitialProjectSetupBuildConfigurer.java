package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.LocalLibrary;
import org.gradle.builds.model.Project;
import org.gradle.builds.model.PublishedLibrary;

public class InitialProjectSetupBuildConfigurer implements BuildConfigurer {
    private final BuildConfigurer configurer;

    public InitialProjectSetupBuildConfigurer(BuildConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public void populate(Build build) {
        StructureAssembler structureAssembler = new StructureAssembler();
        structureAssembler.arrangeProjects(build, build.getProjectInitializer());
        structureAssembler.arrangeClasses(build);

        // Define publications
        for (Project project : build.getProjects()) {
            project.setVersion(build.getVersion());
        }
        if (build.getPublicationTarget() != null) {
            for (Project project : build.getProjects()) {
                project.publishAs(build.getPublicationTarget());
            }
        }

        // Add incoming dependencies
        for (Build other : build.getDependsOn()) {
            if (other.getPublicationTarget().getHttpRepository() != null) {
                build.getRootProject().getBuildScript().allProjects().maven(other.getPublicationTarget().getHttpRepository());
            }
            for (Project project : build.getProjects()) {
                project.requires(other.getPublishedLibraries());
            }
        }

        configurer.populate(build);

        // Collect published libraries
        if (build.getPublicationTarget() != null) {
            for (Project project : build.getProjects()) {
                for (PublishedLibrary<?> publishedLibrary : project.getPublishedLibraries()) {
                    build.publishLibrary(publishedLibrary);
                }
            }
        }
    }
}
