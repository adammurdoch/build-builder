package org.gradle.builds.assemblers;

import org.gradle.builds.model.Application;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Library;
import org.gradle.builds.model.Project;

public class StructureAssembler {
    private final ProjectDecorator decorator;

    public StructureAssembler(ProjectDecorator decorator) {
        this.decorator = decorator;
    }

    public void populate(Settings settings, Build build) {
        defineProjects(settings, build);
        arrangeClasses(settings, build);
    }

    private void arrangeClasses(Settings settings, Build build) {
        Graph classGraph = new Graph();
        new GraphAssembler().arrange(settings.getSourceFileCount(), classGraph);
        System.out.println("* Arranging source files in " + classGraph.getLayers().size() + " layers per project.");
        for (Project project : build.getProjects()) {
            project.setClassGraph(classGraph);
        }
    }

    private void defineProjects(Settings settings, Build build) {
        Graph projectGraph = new Graph();
        new GraphAssembler().arrange(settings.getProjectCount(), projectGraph);
        System.out.println("* Arranging projects in " + projectGraph.getLayers().size() + " layers.");

        projectGraph.visit((Graph.Visitor<Project>) (layer, item, lastLayer, dependencies) -> {
            Project project;
            if (layer == 0) {
                project = build.getRootProject();
                decorator.apply(Application.class, project);
            } else {
                String name;
                if (lastLayer) {
                    name = "core" + (item + 1);
                } else {
                    name = "lib" + layer + "_" + (item + 1);
                }
                project = build.addProject(name);
                decorator.apply(Library.class, project);
            }
            for (Project dep : dependencies) {
                project.dependsOn(dep);
            }
            return project;
        });
    }
}
