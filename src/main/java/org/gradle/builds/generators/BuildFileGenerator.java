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
                if (buildScript.isUseMavenLocalForBuildScriptClasspath()) {
                    printWriter.println("        mavenLocal()");
                }
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
            if (buildScript.getAllProjects() != null) {
                printWriter.println();
                printWriter.println("allprojects {");
                if (!buildScript.getAllProjects().getRepositories().isEmpty()) {
                    printWriter.println("    repositories {");
                    for (ScriptBlock repoBlock : buildScript.getAllProjects().getRepositories()) {
                        writeBlock(repoBlock, "        ", printWriter);
                    }
                    printWriter.println("    }");
                }
                writeBlockContents(buildScript.getAllProjects(), "    ", printWriter);
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
            writeBlockContents(buildScript, "", printWriter);
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

    private void writeBlock(ScriptBlock block, String indent, PrintWriter printWriter) {
        printWriter.println(indent + block.getName() + " {");
        doWriteBlockContents(block, indent + "    ", printWriter, false);
        printWriter.println(indent + "}");
    }

    private void writeBlockContents(Scope block, String indent, PrintWriter printWriter) {
        doWriteBlockContents(block, indent, printWriter, true);
    }

    private void doWriteBlockContents(Scope block, String indent, PrintWriter printWriter, boolean leadingBlankLine) {
        if (!block.getStatements().isEmpty()) {
            if (leadingBlankLine) {
                printWriter.println();
            }
            for (Scope.Expression expression : block.getStatements()) {
                printWriter.print(indent);
                expression.appendTo(printWriter);
                printWriter.println();
            }
            leadingBlankLine = true;
        }
        for (ScriptBlock childBlock : block.getBlocks()) {
            if (leadingBlankLine) {
                printWriter.println();
            }
            leadingBlankLine = true;
            writeBlock(childBlock, indent, printWriter);
        }
    }
}
