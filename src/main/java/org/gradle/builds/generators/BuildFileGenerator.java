package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.ScriptBlock;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BuildFileGenerator {
    public void generate(Build build) throws IOException {
        Path buildFile = build.getRootDir().resolve("build.gradle");
        Files.createDirectories(buildFile.getParent());

        BuildScript buildScript = build.getRootProject().getBuildScript();
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
            for (ScriptBlock scriptBlock : buildScript.getBlocks()) {
                printWriter.println();
                printWriter.println(scriptBlock.getName() + " {");
                for (Map.Entry<String, Object> entry : scriptBlock.getProperties().entrySet()) {
                    if (entry.getValue() instanceof Number) {
                        printWriter.println("  " + entry.getKey() + " = " + entry.getValue());
                    } else {
                        printWriter.println("  " + entry.getKey() + " = '" + entry.getValue() + "'");
                    }
                }
                printWriter.println("}");
            }
        }
    }
}
