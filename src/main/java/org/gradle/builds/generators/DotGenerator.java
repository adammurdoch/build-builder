package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class DotGenerator implements Generator<Model> {
    @Override
    public void generate(Model model) throws IOException {
        Path dotFile = model.getBuild().getRootDir().resolve("dependencies.dot");
        Files.createDirectories(dotFile.getParent());
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(dotFile))) {
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
        }
    }
}
