package org.gradle.builds.generators

import org.gradle.builds.model.ConfiguredBuild
import org.gradle.builds.model.ConfiguredProject
import org.gradle.builds.model.GradlePluginComponent


class GradlePluginSourceGenerator: ProjectFileGenerator() {
    override fun generate(build: ConfiguredBuild, project: ConfiguredProject, fileGenerator: FileGenerator) {
        val component = project.component(GradlePluginComponent::class.java)
        if (component == null) {
            return
        }
        val javaClass = component.implClass!!
        val sourceFile = project.projectDir.resolve("src/main/java/" + javaClass.name.replace('.', '/') + ".java")
        fileGenerator.generate(sourceFile) { writer ->
            writer.println("// GENERATED SOURCE FILE")
            writer.println("package ${javaClass.packageName};")
            writer.println()
            writer.println("import org.gradle.api.Action;")
            writer.println("import org.gradle.api.Plugin;")
            writer.println("import org.gradle.api.Project;")
            writer.println("import org.gradle.api.Task;")
            writer.println()
            writer.println("public class " + javaClass.simpleName + " implements Plugin<Project> {")
            writer.println("    public void apply(Project p) {")
            writer.println("        Task t = p.getTasks().create(\"show\");")
            writer.println("        t.doLast(new Action<Task>() {")
            writer.println("            public void execute(Task task) {")
            writer.println("                System.out.println(\"howdy\");")
            writer.println("            }")
            writer.println("        });")
            writer.println("    }")
            writer.println("}")
        }

        val testFile = project.projectDir.resolve("src/test/java/" + javaClass.name.replace(".", "/") + "Test.java")
        fileGenerator.generate(testFile) { printWriter ->
            printWriter.println("// GENERATED SOURCE FILE")
            printWriter.println("package ${javaClass.packageName};")
            printWriter.println()
            printWriter.println("public class " + javaClass.simpleName + "Test {")
            printWriter.println("    @org.junit.Test")
            printWriter.println("    public void ok() {")
            printWriter.println("        new " + javaClass.getName() + "();")
            printWriter.println("    }")
            printWriter.println("}")
            printWriter.println()
        }

    }
}