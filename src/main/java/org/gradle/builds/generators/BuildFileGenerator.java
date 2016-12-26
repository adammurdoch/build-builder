package org.gradle.builds.generators;

import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.Project;
import org.gradle.builds.model.Scope;
import org.gradle.builds.model.ScriptBlock;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
                for (String gav : buildScript.getBuildScriptClasspath()) {
                    printWriter.println("        classpath '" + gav + "'");
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
            writeBlock(buildScript, "", printWriter);
        }
    }

    private void writeBlock(Scope block, String indent, PrintWriter printWriter) {
        for (Map.Entry<String, Object> entry : block.getProperties().entrySet()) {
            if (entry.getValue() instanceof Number) {
                printWriter.println(indent + entry.getKey() + " = " + entry.getValue());
            } else {
                printWriter.println(indent + entry.getKey() + " = '" + entry.getValue() + "'");
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
