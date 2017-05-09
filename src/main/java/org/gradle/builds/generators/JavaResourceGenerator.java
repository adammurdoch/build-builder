package org.gradle.builds.generators;

import org.gradle.builds.model.HasJavaSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaResourceGenerator extends ProjectComponentSpecificGenerator<HasJavaSource> {
    public JavaResourceGenerator() {
        super(HasJavaSource.class);
    }

    @Override
    protected void generate(Project project, HasJavaSource component) throws IOException {
        Path resourceFile = project.getProjectDir().resolve("src/main/resources/" + project.getName() + ".properties");
        Files.createDirectories(resourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(resourceFile))) {
            printWriter.println("# GENERATED SOURCE FILE");
            printWriter.println("prop=value");
        }
    }
}
