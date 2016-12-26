package org.gradle.builds.generators;

import org.gradle.builds.model.Build;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class SettingsFileGenerator {
    public void generate(Build build) throws IOException {
        Path settingsFile = build.getRootDir().resolve("settings.gradle");
        Files.createDirectories(settingsFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(settingsFile))) {
            printWriter.println("rootProject.name = '" + build.getRootDir().getFileName() + "'");
        }
    }
}
