package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.HasSwiftSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.nio.file.Path;

public class SwiftPackageManagerManifestGenerator implements Generator<BuildProjectStructureBuilder> {
    @Override
    public void generate(BuildProjectStructureBuilder build, FileGenerator fileGenerator) throws IOException {
        HasSwiftSource component = build.getRootProject().component(HasSwiftSource.class);
        if (component == null) {
            return;
        }
        if (!component.isSwiftPm()) {
            return;
        }

        Path manifestFile = build.getRootDir().resolve("Package.swift");
        fileGenerator.generate(manifestFile, writer -> {
            writer.println("import PackageDescription");
            writer.println();
            writer.println("let package = Package(");
            writer.print("    name: \"");
            writer.print(build.getRootProject().getName());
            writer.println("\",");
            writer.println("    targets: [");
            for (Project project : build.getProjects()) {
                writer.print("        Target(name: \"" + project.getName() + "\"");
                if (!project.getRequiredProjects().isEmpty()) {
                    writer.print(", dependencies: [");
                    // TODO - should use required libraries instead
                    for (Dependency<Project> dep : project.getRequiredProjects()) {
                        writer.print("\"" + dep.getTarget().getName() + "\", ");
                    }
                    writer.print("]");
                }
                writer.println("),");
            }
            writer.println("    ]");
            writer.println(")");
        });
    }
}
