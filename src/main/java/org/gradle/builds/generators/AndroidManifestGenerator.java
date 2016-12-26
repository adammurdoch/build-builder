package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidApplication;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class AndroidManifestGenerator {
    public void generate(Build build) throws IOException {
        Project rootProject = build.getRootProject();
        AndroidApplication androidApplication = rootProject.component(AndroidApplication.class);
        if (androidApplication == null) {
            return;
        }

        Path manifestFile = build.getRootDir().resolve("src/main/AndroidManifest.xml");
        Files.createDirectories(manifestFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(manifestFile))) {
            printWriter.println("<manifest package='" + androidApplication.getPackageName() + "'>");
            printWriter.println("</manifest>");
        }
    }
}
