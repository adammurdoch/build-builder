package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.Scope;
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
            writeBlocks(buildScript, "", printWriter);
        }
    }

    private void writeBlocks(Scope scope, String indent, PrintWriter printWriter) {
        for (ScriptBlock block : scope.getBlocks()) {
            printWriter.println();
            printWriter.println(indent + block.getName() + " {");
            String nestedIndent = indent + "    ";
            for (Map.Entry<String, Object> entry : block.getProperties().entrySet()) {
                if (entry.getValue() instanceof Number) {
                    printWriter.println(nestedIndent + entry.getKey() + " = " + entry.getValue());
                } else {
                    printWriter.println(nestedIndent + entry.getKey() + " = '" + entry.getValue() + "'");
                }
            }
            writeBlocks(block, nestedIndent, printWriter);
            printWriter.println(indent + "}");
        }
    }
}
