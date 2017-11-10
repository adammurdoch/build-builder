package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

public class StructureAssembler {
    public void arrangeClasses(Build build) {
        Settings settings = build.getSettings();
        Graph classGraph = new GraphAssembler().arrange(settings.getSourceFileCount());
        System.out.println("* Arranging source files in " + classGraph.getLayers().size() + " layers per project.");
        for (Project project : build.getProjects()) {
            project.setClassGraph(classGraph);
        }
    }

    public void arrangeProjects(Build build, ProjectInitializer projectInitializer) {
        Settings settings = build.getSettings();
        Graph projectGraph = new GraphAssembler().arrange(settings.getProjectCount());
        System.out.println("* Arranging projects in " + projectGraph.getLayers().size() + " layers.");

        projectGraph.visit((Graph.Visitor<Project>) (nodeDetails, dependencies) -> {
            Project project;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                project = build.getRootProject();
                projectInitializer.initRootProject(project);
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = build.getProjectNamePrefix() + "core" + (item + 1);
                } else {
                    name = build.getProjectNamePrefix() + "lib" + layer + "_" + (item + 1);
                }
                project = build.addProject(name);
                if (nodeDetails.isUseAlternate()) {
                    projectInitializer.initAlternateLibraryProject(project);
                } else {
                    projectInitializer.initLibraryProject(project);
                }
            }
            for (Project dep : dependencies) {
                project.requires(dep);
            }
            return project;
        });
    }
}
