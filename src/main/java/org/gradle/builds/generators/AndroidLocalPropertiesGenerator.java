package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class AndroidLocalPropertiesGenerator implements Generator<Build> {
    @Override
    public void generate(Build build) throws IOException {
        for (Project project : build.getProjects()) {
            if (project.component(AndroidComponent.class) != null) {
                generateLocalProperties(build);
                return;
            }
        }
    }

    private void generateLocalProperties(Build build) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(Files.newOutputStream(build.getRootDir().resolve("local.properties")))) {
            printWriter.println("# GENERATED SOURCE FILE");
            File sdkDir = new File(System.getProperty("user.home"), "Library/Android/sdk");
            if (sdkDir.isDirectory()) {
                printWriter.println("sdk.dir=" + sdkDir);
            }
        }
    }
}
