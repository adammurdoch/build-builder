package org.gradle.builds.generators;

import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.ConfiguredProject;
import org.gradle.builds.model.HasJavaSource;

import java.io.IOException;
import java.nio.file.Path;

public class JavaResourceGenerator extends ProjectComponentSpecificGenerator<HasJavaSource> {
    public JavaResourceGenerator() {
        super(HasJavaSource.class);
    }

    @Override
    protected void generate(ConfiguredBuild build, ConfiguredProject project, HasJavaSource component, FileGenerator fileGenerator) throws IOException {
        Path resourceFile = project.getProjectDir().resolve("src/main/resources/" + project.getName() + ".properties");
        fileGenerator.generate(resourceFile, printWriter -> {
            printWriter.println("# GENERATED SOURCE FILE");
            printWriter.println("prop=value");
        });
    }
}
