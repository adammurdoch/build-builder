package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.HasJavaSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScenarioFileGenerator {
    public void generate(Build build) throws IOException {
        boolean jvmBuild = build.getRootProject().component(HasJavaSource.class) != null;

        Path scenarioFile = build.getRootDir().resolve("performance.scenarios");
        Files.createDirectories(scenarioFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(scenarioFile))) {
            printWriter.println("// GENERATED SCENARIO FILE");
            printWriter.println();
            printWriter.println("assemble {");
            printWriter.println("    tasks = [\":assemble\"]");
            printWriter.println("}");
            printWriter.println();
            printWriter.println("cleanAssemble {");
            printWriter.println("    cleanup-tasks = [\"clean\"]");
            printWriter.println("    tasks = [\":assemble\"]");
            printWriter.println("}");
            if (jvmBuild) {
                String apiClass;
                String implClass;
                if (build.getProjects().size() == 1) {
                    apiClass = "src/main/java/org/gradle/example/App.java";
                    implClass = "src/main/java/org/gradle/example/AppNoDeps1.java";
                } else {
                    apiClass = "core1/src/main/java/org/gradle/example/core1/Core1.java";
                    implClass = "core1/src/main/java/org/gradle/example/core1/CoreNoDeps1.java";
                }

                printWriter.println();
                printWriter.println("// Make a change to the ABI of a deep API class");
                printWriter.println("abiChange {");
                printWriter.println("    tasks = [\":assemble\"]");
                printWriter.println("    apply-abi-change-to = \"" + apiClass + "\"");
                printWriter.println("}");

                printWriter.println();
                printWriter.println("// Make a change to the implementation of a deep API class");
                printWriter.println("nonAbiChange {");
                printWriter.println("    tasks = [\":assemble\"]");
                printWriter.println("    apply-non-abi-change-to = \"" + apiClass + "\"");
                printWriter.println("}");

                printWriter.println();
                printWriter.println("// Make a change to the ABI of a deep implementation class");
                printWriter.println("implChange {");
                printWriter.println("    tasks = [\":assemble\"]");
                printWriter.println("    apply-abi-change-to = \"" + implClass + "\"");
                printWriter.println("}");
            }

            printWriter.println();
        }
    }
}
