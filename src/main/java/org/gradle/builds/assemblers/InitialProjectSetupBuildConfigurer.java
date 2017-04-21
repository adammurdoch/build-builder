package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Library;
import org.gradle.builds.model.Project;

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
        if (build.isPublish()) {
            for (Project project : build.getProjects()) {
                project.setPublishAs("org.gradle.example", "ext_" + project.getName());
            }
        }
        modelAssembler.populate(build);
    }
}
