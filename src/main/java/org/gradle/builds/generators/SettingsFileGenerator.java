package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectTreeBuilder;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.nio.file.Path;

public class SettingsFileGenerator implements Generator<BuildProjectTreeBuilder> {
    @Override
    public void generate(BuildProjectTreeBuilder build, FileGenerator fileGenerator) throws IOException {
        Path settingsFile = build.getRootDir().resolve("settings.gradle");
        fileGenerator.generate(settingsFile, printWriter -> {
            printWriter.println("// GENERATED SETTINGS SCRIPT");
            printWriter.println("rootProject.name = '" + build.getRootProject().getName() + "'");
            if (!build.getSubprojects().isEmpty()) {
                printWriter.println();
                for (Project project : build.getSubprojects()) {
                    printWriter.println("include '" + project.getName() + "'");
                }
            }

            if (!build.getIncludedBuilds().isEmpty()) {
                printWriter.println();
                for (BuildProjectTreeBuilder childBuild : build.getIncludedBuilds()) {
                    printWriter.println("includeBuild '" + build.getRootDir().relativize(childBuild.getRootDir()) + "'");
                }
            }

            if (!build.getSourceBuilds().isEmpty()) {
                printWriter.println();
                printWriter.println("sourceControl.vcsMappings {");
                for (BuildProjectTreeBuilder childBuild : build.getSourceBuilds()) {
                    for (Project project : childBuild.getProjects()) {
                        printWriter.println("    withModule('org.gradle.example:" + project.getName() + "') { details ->");
                        printWriter.println("        from(GitVersionControlSpec) {");
                        printWriter.println("            url = uri('" + childBuild.getRootDir().toUri() + "')");
                        printWriter.println("        }");
                        printWriter.println("    }");
                    }
                }
                printWriter.println("}");
            }
        });
    }
}
