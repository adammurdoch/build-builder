package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.nio.file.Path;

public class DotGenerator implements Generator<Model> {
    @Override
    public void generate(Model model, FileGenerator fileGenerator) throws IOException {
        Path dotFile = model.getBuild().getRootDir().resolve("dependencies.dot");
        fileGenerator.generate(dotFile, writer -> {
            writer.println("digraph builds {");
            writer.println("  graph [rankdir=\"LR\"]");
            for (Build build : model.getBuilds()) {
                writer.println("  subgraph cluster_build_" + build.getName() + " {");
                writer.println("    color = \"blue\"");
                for (Project project : build.getProjects()) {
                    writer.println("    " + project.getName());
                }
                writer.println("  }");
                for (Project project : build.getProjects()) {
                    for (Dependency<Library<?>> library : project.getRequiredLibraries(Object.class)) {
                        writer.print("  ");
                        writer.print(project.getName());
                        writer.print(" -> ");
                        writer.print(library.getTarget().getDisplayName());
                        writer.print(" [color = \"");
                        writer.print(library.isApi() ? "black" : "grey56");
                        writer.println("\"]");
                    }
                }
            }
            writer.println("}");
        });
        Path htmlFile = model.getBuild().getRootDir().resolve("dependencies.html");
        fileGenerator.generate(htmlFile, writer -> {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<div class=\"mermaid\">");
            writer.println("graph LR");
            for (Build build : model.getBuilds()) {
                for (Project project : build.getProjects()) {
                    for (Dependency<Library<?>> library : project.getRequiredLibraries(Object.class)) {
                        writer.print("  ");
                        writer.print(project.getName() + "(" + project.getName() + ")");
                        writer.print(library.isApi() ? " -- API --> " : " --> ");
                        writer.print(library.getTarget().getDisplayName() + "(" + library.getTarget().getDisplayName() + ")");
                        writer.println();
                    }
                }
            }
            writer.println("</div>");
            writer.println("<script src=\"https://unpkg.com/mermaid@7.1.2/dist/mermaid.min.js\"></script>");
            writer.println("<script>mermaid.initialize({startOnLoad:true});</script>");
            writer.println("</body>");
            writer.println("</html>");
        });
    }
}
