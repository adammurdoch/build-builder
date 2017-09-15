package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.HasSwiftSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class XCTestInfoPlistGenerator extends ProjectComponentSpecificGenerator<HasSwiftSource> {
    public XCTestInfoPlistGenerator() {
        super(HasSwiftSource.class);
    }

    @Override
    protected void generate(Build build, Project project, HasSwiftSource component) throws IOException {
        if (!component.getTestFiles().isEmpty()) {
            Path path = project.getProjectDir().resolve("src/test/resources/Info.plist");
            Files.createDirectories(path.getParent());
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
                writer.println("<?xml version='1.0' encoding='UTF-8'?>");
                writer.println("<!DOCTYPE plist PUBLIC '-//Apple//DTD PLIST 1.0//EN' 'http://www.apple.com/DTDs/PropertyList-1.0.dtd'>");
                writer.println("<plist version='1.0'>");
                writer.println("<dict>");
                writer.println("</dict>");
                writer.println("</plist>");
            }
        }
    }
}
