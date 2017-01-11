package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class BuildFileGenerator extends ProjectFileGenerator {
    @Override
    protected void generate(Project project) throws IOException {
        Path buildFile = project.getProjectDir().resolve("build.gradle");
        Files.createDirectories(buildFile.getParent());

        BuildScript buildScript = project.getBuildScript();
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(buildFile))) {
            printWriter.println("// GENERATED BUILD SCRIPT");
            if (!buildScript.getBuildScriptClasspath().isEmpty()) {
                printWriter.println();
                printWriter.println("buildscript {");
                printWriter.println("    repositories {");
                printWriter.println("        jcenter()");
                printWriter.println("    }");
                printWriter.println("    dependencies {");
                for (ExternalDependencyDeclaration dep : buildScript.getBuildScriptClasspath()) {
                    printWriter.println("        classpath '" + dep.getGav() + "'");
                }
                printWriter.println("    }");
                printWriter.println("}");
            }
            if (!buildScript.getPlugins().isEmpty()) {
                printWriter.println();
                for (String pluginId : buildScript.getPlugins()) {
                    printWriter.println("apply plugin: '" + pluginId + "'");
                }
            }
            if (project.getParent() == null) {
                printWriter.println();
                printWriter.println("allprojects {");
                printWriter.println("    repositories {");
                printWriter.println("        jcenter()");
                printWriter.println("    }");
                printWriter.println("}");
            }
            if (!buildScript.getDependencies().isEmpty()) {
                printWriter.println();
                printWriter.println("dependencies {");
                for (Map.Entry<String, Set<DependencyDeclaration>> entry : buildScript.getDependencies().entrySet()) {
                    for (DependencyDeclaration dep : entry.getValue()) {
                        printWriter.println("    " + entry.getKey() + " " + format(dep));
                    }
                }
                printWriter.println("}");
            }
            writeBlock(buildScript, "", printWriter);
            if (!buildScript.getComponentDeclarations().isEmpty()) {
                printWriter.println();
                printWriter.println("model {");
                printWriter.println("    components {");
                for (SoftwareModelDeclaration component : buildScript.getComponentDeclarations()) {
                    printWriter.println("        " + component.getName() + "(" + component.getType() + ") {");
                    printWriter.println("            baseName = project.name");
                    if (!component.getDependencies().isEmpty()) {
                        printWriter.println("            sources {");
                        printWriter.println("                all {");
                        for (ProjectDependencyDeclaration dep : component.getDependencies()) {
                            printWriter.println("                    lib project: '" + dep.getProjectPath() + "', library: 'main'");
                        }
                        printWriter.println("                }");
                        printWriter.println("            }");
                    }
                    printWriter.println("        }");
                }
                printWriter.println("    }");
                printWriter.println("}");
            }
            if (project.getParent() == null) {
                printWriter.println();
                printWriter.println("allprojects {");
                printWriter.println("    tasks.withType(JavaCompile) {");
                printWriter.println("        options.incremental = true");
                printWriter.println("    }");
                printWriter.println("}");
            }
            printWriter.println();
        }
    }

    private String format(DependencyDeclaration dep) {
        if (dep instanceof ProjectDependencyDeclaration) {
            ProjectDependencyDeclaration projectDependencyDeclaration = (ProjectDependencyDeclaration) dep;
            return "project('" + projectDependencyDeclaration.getProjectPath() + "')";
        }
        return "'" + ((ExternalDependencyDeclaration) dep).getGav() + "'";
    }

    private void writeBlock(Scope block, String indent, PrintWriter printWriter) {
        if (!block.getProperties().isEmpty()) {
            printWriter.println();
            for (Map.Entry<String, Object> entry : block.getProperties().entrySet()) {
                if (entry.getValue() instanceof Number) {
                    printWriter.println(indent + entry.getKey() + " = " + entry.getValue());
                } else {
                    printWriter.println(indent + entry.getKey() + " = '" + entry.getValue() + "'");
                }
            }
        }
        for (ScriptBlock childBlock : block.getBlocks()) {
            printWriter.println();
            printWriter.println(indent + childBlock.getName() + " {");
            writeBlock(childBlock, indent + "    ", printWriter);
            printWriter.println(indent + "}");
        }
    }
}
