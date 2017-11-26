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
    protected void generate(Build build, Project project) throws IOException {
        Path buildFile = project.getProjectDir().resolve("build.gradle");
        Files.createDirectories(buildFile.getParent());

        BuildScript buildScript = project.getBuildScript();
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(buildFile))) {
            printWriter.println("// GENERATED BUILD SCRIPT");
            if (!buildScript.getBuildScriptClasspath().isEmpty()) {
                printWriter.println();
                printWriter.println("buildscript {");
                if (buildScript.getBuildScriptBlock() != null) {
                    if (!buildScript.getBuildScriptBlock().getRepositories().isEmpty()) {
                        printWriter.println("    repositories {");
                        for (ScriptBlock repoBlock : buildScript.getBuildScriptBlock().getRepositories()) {
                            writeBlock(repoBlock, "        ", printWriter);
                        }
                        printWriter.println("    }");
                    }
                    writeBlockContents(buildScript.getBuildScriptBlock(), "    ", printWriter);
                }
                printWriter.println("    dependencies {");
                for (ExternalDependencyDeclaration dep : buildScript.getBuildScriptClasspath()) {
                    printWriter.println("        classpath '" + dep.getGav() + "'");
                }
                printWriter.println("    }");
                printWriter.println("}");
            }
            if (!buildScript.getPlugins().isEmpty()) {
                printWriter.println();
                for (ProjectScriptBlock.Plugin plugin : buildScript.getPlugins()) {
                    apply(printWriter, "", plugin);
                }
            }

            ProjectScriptBlock allProjects = buildScript.getAllProjects();
            if (allProjects != null) {
                printWriter.println();
                printWriter.println("allprojects {");
                if (!allProjects.getPlugins().isEmpty()) {
                    for (ProjectScriptBlock.Plugin plugin : allProjects.getPlugins()) {
                        apply(printWriter, "    ", plugin);
                    }
                }
                if (!allProjects.getRepositories().isEmpty()) {
                    printWriter.println("    repositories {");
                    for (ScriptBlock repoBlock : allProjects.getRepositories()) {
                        writeBlock(repoBlock, "        ", printWriter);
                    }
                    printWriter.println("    }");
                }
                writeBlockContents(allProjects, "    ", printWriter);
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
            printWriter.println();
        }
    }

    private void apply(PrintWriter printWriter, String indent, ProjectScriptBlock.Plugin plugin) {
        if (plugin.getVersion() == null) {
            printWriter.print(indent);
            printWriter.println("apply plugin: '" + plugin.getId() + "'");
        } else {
            printWriter.print(indent);
            printWriter.println("if (gradle.gradleVersion.startsWith('" + plugin.getVersion() + "')) { apply plugin: '" + plugin.getId() + "' }");
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
        if (block.getBlocks().isEmpty() && block.getStatements().isEmpty()) {
            printWriter.println(indent + block.getName() + "()");
            return;
        }
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
