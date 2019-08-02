package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.HasJavaSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.nio.file.Path;

public class JavaResourceGenerator extends ProjectComponentSpecificGenerator<HasJavaSource> {
    public JavaResourceGenerator() {
        super(HasJavaSource.class);
    }

    @Override
    protected void generate(BuildProjectStructureBuilder build, Project project, HasJavaSource component, FileGenerator fileGenerator) throws IOException {
        Path resourceFile = project.getProjectDir().resolve("src/main/resources/" + project.getName() + ".properties");
        fileGenerator.generate(resourceFile, printWriter -> {
            printWriter.println("# GENERATED SOURCE FILE");
            printWriter.println("prop=value");
        });
    }
}
