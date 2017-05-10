package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class InitialProjectSetupBuildConfigurer implements ModelAssembler {
    private final ModelAssembler modelAssembler;

    public InitialProjectSetupBuildConfigurer(ModelAssembler modelAssembler) {
        this.modelAssembler = modelAssembler;
    }

    @Override
    public void apply(Class<? extends Component> component, Project project) {
        modelAssembler.apply(component, project);
    }

    @Override
    public void populate(Build build) {
        StructureAssembler structureAssembler = new StructureAssembler();
        structureAssembler.arrangeProjects(build);
        structureAssembler.arrangeClasses(build);

        if (build.getRootProjectType() != null) {
            modelAssembler.apply(build.getRootProjectType(), build.getRootProject());
        }
        for (Project project : build.getSubprojects()) {
            modelAssembler.apply(Library.class, project);
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
                project.dependsOn(other.getPublishedLibraries());
            }
        }

        modelAssembler.populate(build);

        // Collect published libraries
        if (build.getPublicationTarget() != null) {
            for (Project project : build.getProjects()) {
                PublishedJvmLibrary jvmLibrary = project.component(PublishedJvmLibrary.class);
                if (jvmLibrary != null) {
                    build.publishLibrary(jvmLibrary);
                }
            }
        }
    }
}
