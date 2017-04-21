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

        if (build.getHttpRepository() != null) {
            for (Project project : build.getProjects()) {
                project.setPublishAs("org.gradle.example", "ext_" + project.getName());
            }
        }

        for (Build other : build.getDependsOn()) {
            build.getRootProject().getBuildScript().allProjects().maven(other.getHttpRepository());
            for (Project project : build.getProjects()) {
                for (ExternalJvmLibrary library : other.getPublishedLibraries()) {
                    project.getBuildScript().dependsOn("compile", library.getGav());
                }
            }
        }

        modelAssembler.populate(build);

        if (build.getHttpRepository() != null) {
            for (Project project : build.getProjects()) {
                JvmLibrary jvmLibrary = project.component(JvmLibrary.class);
                if (jvmLibrary != null) {
                    build.publishLibrary(new ExternalJvmLibrary(
                            new ExternalDependencyDeclaration(project.getPublishGroup() + ":" + project.getPublishModule() + ":1.2")));
                }
            }
        }
    }
}
