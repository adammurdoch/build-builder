package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.Project;

import java.io.File;
import java.io.IOException;

public class AndroidLocalPropertiesGenerator implements Generator<BuildProjectStructureBuilder> {
    @Override
    public void generate(BuildProjectStructureBuilder build, FileGenerator fileGenerator) throws IOException {
        for (Project project : build.getProjects()) {
            if (project.component(AndroidComponent.class) != null) {
                generateLocalProperties(build, fileGenerator);
                return;
            }
        }
    }

    private void generateLocalProperties(BuildProjectStructureBuilder build, FileGenerator fileGenerator) throws IOException {
        fileGenerator.generate(build.getRootDir().resolve("local.properties"), printWriter -> {
            printWriter.println("# GENERATED SOURCE FILE");
            File sdkDir = new File(System.getProperty("user.home"), "Library/Android/sdk");
            if (sdkDir.isDirectory()) {
                printWriter.println("sdk.dir=" + sdkDir);
            }
        });
    }
}
