package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class AndroidManifestGenerator extends ProjectFileGenerator {
    @Override
    protected void generate(Project project) throws IOException {
        AndroidComponent androidComponent = project.component(AndroidComponent.class);
        if (androidComponent == null) {
            return;
        }

        Path manifestFile = project.getProjectDir().resolve("src/main/AndroidManifest.xml");
        Files.createDirectories(manifestFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(manifestFile))) {
            printWriter.println("<!-- GENERATED SOURCE FILE -->");
            printWriter.println("<manifest package='" + androidComponent.getPackageName() + "'>");
            printWriter.println("</manifest>");
        }
    }
}
