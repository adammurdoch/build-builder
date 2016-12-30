package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureAssembler {
    public void populate(Settings settings, Build build) {
        Project rootProject = build.getRootProject();
        rootProject.setRole(Project.Role.Application);

        Graph graph = new Graph();
        new GraphAssembler().arrange(settings.getProjectCount(), graph);
        System.out.println("* Arranging projects in " + graph.getLayers().size() + " layers.");

        if (graph.getNodes().size() == 1) {
            return;
        }

        Map<Graph.Node, Project> projects = new HashMap<>();
        projects.put(graph.getRoot(), rootProject);

        int lastLayer = graph.getLayers().size() - 1;
        for (int layer = lastLayer; layer > 0; layer--) {
            List<Graph.Node> nodes = graph.getLayers().get(layer);
            String name = layer == lastLayer ? "core" : "lib" + (layer + 1) + "_";
            for (int item = 0; item < nodes.size(); item++) {
                Graph.Node node = nodes.get(item);
                Project project = build.addProject(name + (item + 1));
                project.setRole(Project.Role.Library);
                projects.put(node, project);
                for (Graph.Node dep : node.getDependsOn()) {
                    project.dependsOn(projects.get(dep));
                }
            }
        }

        for (Graph.Node dep : graph.getRoot().getDependsOn()) {
            rootProject.dependsOn(projects.get(dep));
        }
    }
}
