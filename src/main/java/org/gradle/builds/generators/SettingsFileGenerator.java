package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsFileGenerator implements BuildGenerator {
    public void generate(Build build) throws IOException {
        Path settingsFile = build.getRootDir().resolve("settings.gradle");
        Files.createDirectories(settingsFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(settingsFile))) {
            printWriter.println("// GENERATED SETTINGS SCRIPT");
            printWriter.println("rootProject.name = '" + build.getRootProject().getName() + "'");
            if (!build.getSubprojects().isEmpty()) {
                printWriter.println();
                for (Project project : build.getSubprojects()) {
                    printWriter.println("include '" + project.getName() + "'");
                }
            }

            if (!build.getChildBuilds().isEmpty()) {
                printWriter.println();
                for (Build childBuild : build.getChildBuilds()) {
                    printWriter.println("includeBuild '" + build.getRootDir().relativize(childBuild.getRootDir()) + "'");
                }
            }
        }
    }
}
