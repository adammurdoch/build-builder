package org.gradle.builds.generators;

import org.gradle.builds.model.HasSwiftSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwiftPackageManagerManifestGenerator extends ProjectComponentSpecificGenerator<HasSwiftSource> {
    public SwiftPackageManagerManifestGenerator() {
        super(HasSwiftSource.class);
    }

    @Override
    protected void generate(Project project, HasSwiftSource component) throws IOException {
        if (!component.isSwiftPm()) {
            return;
        }

        Path manifestFile = project.getProjectDir().resolve("Package.swift");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(manifestFile))) {
            writer.println("import PackageDescription");
            writer.println();
            writer.println("let package = Package(");
            writer.print("    name: \"");
            writer.print(project.getName());
            writer.print("\"");
            if (!project.getDependencies().isEmpty()) {
                writer.println(",");
                writer.println("    dependencies: [");
                for (Project dep : project.getDependencies()) {
                    writer.println("        .package(url: \"" + dep.getName() + "\")");
                }
                writer.print("    ]");
            }
            writer.println();
            writer.println(")");
        }
    }
}
