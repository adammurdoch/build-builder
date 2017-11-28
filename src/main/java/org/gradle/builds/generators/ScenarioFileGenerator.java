package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.CppSourceFile;
import org.gradle.builds.model.HasCppSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ScenarioFileGenerator implements Generator<Build> {
    public void generate(Build build) throws IOException {
        Path scenarioFile = build.getRootDir().resolve("performance.scenarios");
        Files.createDirectories(scenarioFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(scenarioFile))) {
            printWriter.println("// GENERATED SCENARIO FILE");
            if (build.getRootProject().component(HasCppSource.class) != null) {
                scenario("assemble", printWriter, false, p ->{
                    printWriter.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                });
                scenario("cleanAssemble", printWriter, true, p ->{
                    printWriter.println("    cleanup-tasks = [\"clean\"]");
                    printWriter.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                });
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
                String finalProjectDir = projectDir;
                scenario("abiAssemble", printWriter, false, p ->{
                    printWriter.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                    printWriter.print("    apply-h-change-to = \"");
                    printWriter.print(finalProjectDir);
                    printWriter.print(header);
                    printWriter.println("\"");
                });
                scenario("nonAbiAssemble", printWriter, false, p -> {
                    printWriter.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                    printWriter.print("    apply-cpp-change-to = \"");
                    printWriter.print(finalProjectDir);
                    printWriter.print("src/main/cpp/");
                    printWriter.print(sourceFile.getName());
                    printWriter.println("\"");
                });
            }

            printWriter.println();
        }
    }

    private void scenario(String name, PrintWriter printWriter, boolean cache, Consumer<PrintWriter> body) {
        printWriter.println();
        printWriter.println(name + " {");
        body.accept(printWriter);
        printWriter.println("}");
        printWriter.println();
        printWriter.println(name + "Parallel {");
        printWriter.println("    gradle-args = [\"--parallel\"]");
        body.accept(printWriter);
        printWriter.println("}");
        if (cache) {
            printWriter.println();
            printWriter.println(name + "ParallelCache {");
            printWriter.println("    gradle-args = [\"--parallel\", \"--build-cache\", \"-Dorg.gradle.caching.native=true\"]");
            body.accept(printWriter);
            printWriter.println("}");
        }
    }
}
