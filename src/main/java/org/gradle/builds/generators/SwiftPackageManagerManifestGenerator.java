package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Dependency;
import org.gradle.builds.model.HasSwiftSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwiftPackageManagerManifestGenerator implements Generator<Build> {
    @Override
    public void generate(Build build) throws IOException {
        HasSwiftSource component = build.getRootProject().component(HasSwiftSource.class);
        if (component == null) {
            return;
        }
        if (!component.isSwiftPm()) {
            return;
        }

        Path manifestFile = build.getRootDir().resolve("Package.swift");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(manifestFile))) {
            writer.println("import PackageDescription");
            writer.println();
            writer.println("let package = Package(");
            writer.print("    name: \"");
            writer.print(build.getRootProject().getName());
            writer.println("\",");
            writer.println("    targets: [");
            for (Project project : build.getProjects()) {
                writer.print("        Target(name: \"" + project.getName() + "\"");
                if (!project.getDependencies().isEmpty()) {
                    writer.print(", dependencies: [");
                    // TODO - should use required libraries instead
                    for (Dependency<Project> dep : project.getDependencies()) {
                        writer.print("\"" + dep.getTarget().getName() + "\", ");
                    }
                    writer.print("]");
                }
                writer.println("),");
            }
            writer.println("    ]");
            writer.println(")");
        }
    }
}
