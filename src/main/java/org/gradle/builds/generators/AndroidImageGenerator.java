package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidApplication;
import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.ConfiguredProject;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class AndroidImageGenerator extends ProjectComponentSpecificGenerator<AndroidApplication> {
    public AndroidImageGenerator() {
        super(AndroidApplication.class);
    }

    @Override
    protected void generate(ConfiguredBuild build, ConfiguredProject project, AndroidApplication component, FileGenerator fileGenerator) throws IOException {
        Path outFile = project.getProjectDir().resolve("src/main/res/mipmap-hdpi/ic_launcher.png");
        try (InputStream imageContent = getClass().getClassLoader().getResourceAsStream("ic_launcher.png")) {
            fileGenerator.generate(outFile, imageContent);
        }
    }
}
