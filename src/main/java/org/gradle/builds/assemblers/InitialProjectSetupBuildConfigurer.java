package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class InitialProjectSetupBuildConfigurer implements BuildConfigurer<BuildProjectStructureBuilder> {
    private final GraphAssembler graphAssembler;

    public InitialProjectSetupBuildConfigurer(GraphAssembler graphAssembler) {
        this.graphAssembler = graphAssembler;
    }

    @Override
    public void populate(BuildProjectStructureBuilder build) {
        StructureAssembler structureAssembler = new StructureAssembler(graphAssembler);
        structureAssembler.arrangeProjects(build, build.getProjectInitializer());
        structureAssembler.arrangeClasses(build);

        // Define publications
        for (Project project: build.getProjects()) {
            project.setVersion(build.getVersion());
        }
        for (Project project: build.getProjects()) {
            HasApi library = project.component(HasApi.class);
            if (library != null) {
                if (build.getPublicationTarget() != null) {
                    project.publishAs(build.getPublicationTarget());
                    String group = "org.gradle.example";
                    String module = project.getName();
                    String version = project.getVersion();
                    project.export(new LocalLibrary<>(project, new ExternalDependencyDeclaration(group, module, version), library.getApi()));
                } else {
                    project.export(new LocalLibrary<>(project, null, library.getApi()));
                }
            }
        }

        // Add incoming dependencies
        for (BuildProjectStructureBuilder other: build.getDependsOn()) {
            if (other.getPublicationTarget().getHttpRepository() != null) {
                build.getRootProject().getBuildScript().allProjects().maven(other.getPublicationTarget().getHttpRepository());
            }
            for (Project project: build.getProjects()) {
                project.requires(other.getExportedLibraries());
            }
        }
    }
}
