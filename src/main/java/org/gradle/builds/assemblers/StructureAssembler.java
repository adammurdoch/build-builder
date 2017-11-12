package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.Project;

public class StructureAssembler {
    public void arrangeClasses(Build build) {
        Settings settings = build.getSettings();
        Graph classGraph = new GraphAssembler().arrange(settings.getSourceFileCount());
        System.out.println("* Arranging source files in " + classGraph.getLayers() + " layers per project.");
        for (Project project : build.getProjects()) {
            project.setClassGraph(classGraph);
        }
    }

    public void arrangeProjects(Build build, ProjectInitializer projectInitializer) {
        Settings settings = build.getSettings();
        Graph projectGraph = new GraphAssembler().arrange(settings.getProjectCount());
        System.out.println("* Arranging projects in " + projectGraph.getLayers() + " layers.");

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
            for (Dependency<Project> dep : dependencies) {
                project.requires(dep);
            }
            if (nodeDetails.isDeepest()) {
                build.setDeepestProject(project);
            }
            return project;
        });
    }
}
