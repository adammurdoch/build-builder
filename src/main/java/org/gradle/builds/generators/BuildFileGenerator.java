package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.ScriptBlock;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class BuildFileGenerator {
    public void generate(Build build) throws IOException {
        Path settingsFile = build.getRootDir().resolve("build.gradle");
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(settingsFile))) {
            printWriter.println("// GENERATED BUILD SCRIPT");
            if (!build.getRootProject().getBuildScriptClasspath().isEmpty()) {
                printWriter.println();
                printWriter.println("buildscript {");
                printWriter.println("    repositories {");
                printWriter.println("        jcenter()");
                printWriter.println("    }");
                printWriter.println("    dependencies {");
                for (String gav : build.getRootProject().getBuildScriptClasspath()) {
                    printWriter.println("        classpath '" + gav + "'");
                }
                printWriter.println("    }");
                printWriter.println("}");
            }
            if (!build.getRootProject().getPlugins().isEmpty()) {
                printWriter.println();
                for (String pluginId : build.getRootProject().getPlugins()) {
                    printWriter.println("apply plugin: '" + pluginId + "'");
                }
            }
            for (ScriptBlock scriptBlock : build.getRootProject().getBlocks()) {
                printWriter.println();
                printWriter.println(scriptBlock.getName() + " {");
                for (Map.Entry<String, String> entry : scriptBlock.getProperties().entrySet()) {
                    printWriter.println("  " + entry.getKey() + " = '" + entry.getValue() + "'");
                }
                printWriter.println("}");
            }
        }
    }
}
