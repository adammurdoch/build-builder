package org.gradle.builds.generators

import org.gradle.builds.model.Build
import org.gradle.builds.model.HasCppSource
import org.gradle.builds.model.HasSource
import java.io.PrintWriter

class ScenarioFileGenerator : Generator<Build> {
    override fun generate(build: Build, fileGenerator: FileGenerator) {
        val scenarioFile = build.rootDir.resolve("performance.scenarios")
        fileGenerator.generate(scenarioFile) { printWriter ->
            printWriter.println("// GENERATED SCENARIO FILE")
            if (build.rootProject.component(HasSource::class.java) != null) {
                scenario("assemble", printWriter) {
                    println("    tasks = [\"assemble\"]")
                }
                scenario("build", printWriter) {
                    println("    tasks = [\"build\"]")
                }
            }
            if (build.rootProject.component(HasCppSource::class.java) != null) {
                scenario("cleanAssemble", printWriter) {
                    println("    cleanup-tasks = [\"clean\"]")
                    println("    tasks = [\"assemble\"]")
                }
                val deepestProject = build.deepestProject
                val component = deepestProject.component(HasCppSource::class.java)
                val header = if (component.publicHeaderFiles.isNotEmpty()) {
                    "src/main/public/" + component.publicHeaderFiles.iterator().next().name
                } else {
                    "src/main/headers/" + component.implementationHeaderFiles.iterator().next().name
                }
                val sourceFile = component.sourceFiles.iterator().next()
                var projectDir = build.rootDir.relativize(deepestProject.projectDir).toString()
                if (projectDir.isNotEmpty()) {
                    projectDir = "$projectDir/"
                }
                scenario("abiAssemble", printWriter) {
                    println("    tasks = [\"assemble\"]")
                    println("    apply-h-change-to = \"${projectDir}${header}\"")
                }
                scenario("nonAbiAssemble", printWriter) {
                    println("    tasks = [\"assemble\"]")
                    println("    apply-cpp-change-to = \"${projectDir}src/main/cpp/${sourceFile.name}\"")
                }
            }

            printWriter.println()
        }
    }

    private fun scenario(name: String, printWriter: PrintWriter, body: PrintWriter.() -> Unit) {
        printWriter.println()
        printWriter.println("$name {")
        printWriter.println("    gradle-args = [\"--parallel\"]")
        body(printWriter)
        printWriter.println("}")
        printWriter.println()
        printWriter.println("${name}InstantExecution {")
        printWriter.println("    gradle-args = [\"--parallel\", \"-Dorg.gradle.unsafe.instant-execution=true\"]")
        body(printWriter)
        printWriter.println("}")
        printWriter.println()
        printWriter.println("${name}DryRun {")
        printWriter.println("    gradle-args = [\"--parallel\", \"--dry-run\"]")
        body(printWriter)
        printWriter.println("}")
        printWriter.println()
        printWriter.println("${name}InstantExecutionDryRun {")
        printWriter.println("    gradle-args = [\"--parallel\", \"--dry-run\", \"-Dorg.gradle.unsafe.instant-execution=true\"]")
        body(printWriter)
        printWriter.println("}")
    }
}
