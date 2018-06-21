package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.Project;

public class StructureAssembler {

    private final GraphAssembler graphAssembler;

    public StructureAssembler(GraphAssembler graphAssembler) {
        this.graphAssembler = graphAssembler;
    }

    public void arrangeClasses(Build build) {
        Settings settings = build.getSettings();
        Graph classGraph = graphAssembler.arrange(settings.getSourceFileCount());
        for (Project project : build.getProjects()) {
            project.setClassGraph(classGraph);
        }
    }

    public void arrangeProjects(Build build, ProjectInitializer projectInitializer) {
        Settings settings = build.getSettings();
        Graph projectGraph = graphAssembler.arrange(settings.getProjectCount());

        projectGraph.visit((Graph.Visitor<Project>) (nodeDetails, dependencies) -> {
            Project project;
            int layer = nodeDetails.getLayer();
            if (layer == 0) {
                project = build.getRootProject();
                project.setTypeName("App");
                projectInitializer.initRootProject(project);
            } else {
                String typeName = build.getTypeNamePrefix() + "Lib" + nodeDetails.getNameSuffix();
                project = build.addProject(typeName.toLowerCase());
                project.setTypeName(typeName);
                if (nodeDetails.isUseAlternate()) {
                    projectInitializer.initAlternateLibraryProject(project);
                } else {
                    projectInitializer.initLibraryProject(project);
                }
            }
            if (nodeDetails.isExported()) {
                build.exportProject(project);
            }
            for (Dependency<Project> dep : dependencies) {
                project.requiresProject(dep);
            }
            if (nodeDetails.isDeepest()) {
                build.setDeepestProject(project);
            }
            return project;
        });
    }
}
