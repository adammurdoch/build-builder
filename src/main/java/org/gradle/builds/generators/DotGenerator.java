package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.nio.file.Path;

public class DotGenerator implements Generator<BuildTree> {
    @Override
    public void generate(BuildTree model, FileGenerator fileGenerator) throws IOException {
        Path htmlFile = model.getBuild().getRootDir().resolve("dependencies.html");
        fileGenerator.generate(htmlFile, writer -> {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<style>");
            writer.println("body { font-family: sans-serif; margin: 40px; }");
            writer.println("div.mermaid { margin-bottom: 40px; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");

            writer.println("<h2>Build dependencies</h2>");
            writer.println("<div class=\"mermaid\">");
            writer.println("graph LR");
            for (Build build : model.getBuilds()) {
                writer.print("  ");
                writer.println(build.getName());
                for (Build dep : build.getDependsOn()) {
                    writer.print("  ");
                    writer.print(build.getName());
                    writer.print(" --> ");
                    writer.print(dep.getName());
                    writer.println();
                }
            }
            writer.println("</div>");

            writer.println("<h2>Project dependencies</h2>");
            writer.println("<div class=\"mermaid\">");
            writer.println("graph LR");
            for (Build build : model.getBuilds()) {
                writer.println("  subgraph " + build.getName());
                for (Project project : build.getProjects()) {
                    writer.print("  ");
                    writer.print(project.getName());
                    writer.println();
                }
                writer.println("  end");
                for (Project project : build.getProjects()) {
                    for (Dependency<Library<?>> library : project.getRequiredLibraries(Object.class)) {
                        writer.print("  ");
                        writer.print(project.getName());
                        writer.print(library.isApi() ? " -- API --> " : " --> ");
                        writer.print(library.getTarget().getDisplayName());
                        writer.println();
                    }
                }
            }
            writer.println("</div>");
            writer.println("<script src=\"https://unpkg.com/mermaid@8.0.0-rc.8/dist/mermaid.min.js\"></script>");
            writer.println("<script>");
            writer.println("mermaid.initialize({startOnLoad:true});");
            writer.println("</script>");
            writer.println("</body>");
            writer.println("</html>");
        });
    }
}
