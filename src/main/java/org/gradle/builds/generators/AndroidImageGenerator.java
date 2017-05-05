package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidApplication;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AndroidImageGenerator extends ProjectFileGenerator {
    @Override
    protected void generate(Project project) throws IOException {
        AndroidApplication application = project.component(AndroidApplication.class);
        if (application != null) {
            Path outFile = project.getProjectDir().resolve("src/main/res/mipmap-hdpi/ic_launcher.png");
            Files.createDirectories(outFile.getParent());
            try (InputStream imageContent = getClass().getClassLoader().getResourceAsStream("ic_launcher.png")) {
                Files.copy(imageContent, outFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
