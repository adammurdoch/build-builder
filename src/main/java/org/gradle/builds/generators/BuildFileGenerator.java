package org.gradle.builds.generators;

import org.gradle.builds.model.Build;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class BuildFileGenerator {
    public void generate(Build build) throws IOException {
        Path settingsFile = build.getRootDir().resolve("build.gradle");
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(settingsFile))) {
            for (String pluginId : build.getRootProject().getPlugins()) {
                printWriter.println("apply plugin: '" + pluginId + "'");
            }
        }
    }
}
