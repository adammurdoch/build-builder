package org.gradle.builds.generators;

import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.ConfiguredProject;
import org.gradle.builds.model.HasSwiftSource;

import java.io.IOException;
import java.nio.file.Path;

public class XCTestInfoPlistGenerator extends ProjectComponentSpecificGenerator<HasSwiftSource> {
    public XCTestInfoPlistGenerator() {
        super(HasSwiftSource.class);
    }

    @Override
    protected void generate(ConfiguredBuild build, ConfiguredProject project, HasSwiftSource component, FileGenerator fileGenerator) throws IOException {
        if (!component.getTestFiles().isEmpty()) {
            Path path = project.getProjectDir().resolve("src/test/resources/Info.plist");
            fileGenerator.generate(path, writer -> {
                writer.println("<?xml version='1.0' encoding='UTF-8'?>");
                writer.println("<!DOCTYPE plist PUBLIC '-//Apple//DTD PLIST 1.0//EN' 'http://www.apple.com/DTDs/PropertyList-1.0.dtd'>");
                writer.println("<plist version='1.0'>");
                writer.println("<dict>");
                writer.println("</dict>");
                writer.println("</plist>");
            });
        }
    }
}
