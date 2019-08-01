package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ScenarioFileGenerator implements Generator<Build> {
    @Override
    public void generate(Build build, FileGenerator fileGenerator) throws IOException {
        Path scenarioFile = build.getRootDir().resolve("performance.scenarios");
        fileGenerator.generate(scenarioFile, printWriter -> {
            printWriter.println("// GENERATED SCENARIO FILE");
            if (build.getRootProject().component(HasCppSource.class) != null) {
                scenario("assemble", printWriter, false, p -> {
                    p.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                });
                scenario("cleanAssemble", printWriter, true, p -> {
                    p.println("    cleanup-tasks = [\"clean\"]");
                    p.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
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
                scenario("abiAssemble", printWriter, false, p -> {
                    p.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                    p.print("    apply-h-change-to = \"");
                    p.print(finalProjectDir);
                    p.print(header);
                    p.println("\"");
                });
                scenario("nonAbiAssemble", printWriter, false, p -> {
                    p.println("    tasks = [\":linkDebug\", \":linkRelease\"]");
                    p.print("    apply-cpp-change-to = \"");
                    p.print(finalProjectDir);
                    p.print("src/main/cpp/");
                    p.print(sourceFile.getName());
                    p.println("\"");
                });
            } else if (build.getRootProject().component(HasJavaSource.class) != null) {
                scenario("assemble", printWriter, false, p -> {
                    p.println("    tasks = [\"assemble\"]");
                });
                scenario("build", printWriter, false, p -> {
                    p.println("    tasks = [\"build\"]");
                });
            }

            printWriter.println();
        });
    }

    private void scenario(String name, PrintWriter printWriter, boolean cache, Consumer<PrintWriter> body) {
        printWriter.println();
        printWriter.println(name + " {");
        body.accept(printWriter);
        printWriter.println("}");
        printWriter.println();
        printWriter.println(name + "InstantExecutionParallel {");
        printWriter.println("    gradle-args = [\"--parallel\", \"-Dorg.gradle.unsafe.instant-execution=true\"]");
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
