package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.CppSourceFile;
import org.gradle.builds.model.HasCppSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScenarioFileGenerator implements Generator<Build> {
    public void generate(Build build) throws IOException {
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

            if (build.getRootProject().component(HasCppSource.class) != null) {
                Project deepestProject = build.getDeepestProject();
                HasCppSource component = deepestProject.component(HasCppSource.class);
                String header;
                if (!component.getPublicHeaderFiles().isEmpty()) {
                    header = "src/main/public/" + component.getPublicHeaderFiles().iterator().next().getName();
                } else {
                    header = "src/main/headers/" + component.getImplementationHeaderFiles().iterator().next().getName();
                }
                CppSourceFile sourceFile = component.getSourceFiles().iterator().next();
                String projectDir = build.getRootDir().relativize(deepestProject.getProjectDir()).toString();
                if (!projectDir.isEmpty()) {
                    projectDir = projectDir + "/";
                }
                printWriter.println();
                printWriter.println("abiAssemble {");
                printWriter.println("    tasks = [\":assemble\"]");
                printWriter.print("    apply-h-change-to = \"");
                printWriter.print(projectDir);
                printWriter.print(header);
                printWriter.println("\"");
                printWriter.println("}");
                printWriter.println();
                printWriter.println("nonAbiAssemble {");
                printWriter.println("    tasks = [\":assemble\"]");
                printWriter.print("    apply-cpp-change-to = \"");
                printWriter.print(projectDir);
                printWriter.print("src/main/cpp/");
                printWriter.print(sourceFile.getName());
                printWriter.println("\"");
                printWriter.println("}");
            }

            printWriter.println();
        }
    }
}
