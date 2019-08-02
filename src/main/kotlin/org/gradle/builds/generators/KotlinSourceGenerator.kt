package org.gradle.builds.generators

import org.gradle.builds.model.*

class KotlinSourceGenerator : ProjectComponentSpecificGenerator<HasKotlinSource>(HasKotlinSource::class.java) {
    override fun generate(build: BuildProjectStructureBuilder, project: Project, component: HasKotlinSource, fileGenerator: FileGenerator) {
        for (kotlinClass in component.sourceFiles) {
            generateMainClass(project, kotlinClass, fileGenerator)
        }
    }

    private fun generateMainClass(project: Project, kotlinClass: KotlinClass, fileGenerator: FileGenerator) {
        val sourceFile = project.projectDir.resolve("src/main/kotlin/" + kotlinClass.name.replace(".", "/") + ".kt")
        fileGenerator.generate(sourceFile) {
            it.println("// GENERATED SOURCE FILE")
            it.println("package ${kotlinClass.packageName}")
            it.println()
            if (kotlinClass.role(AppEntryPoint::class.java) != null) {
                it.println("fun main(args: Array<String>) {")
                it.println("  app.doSomething()")
                it.println("  app.doSomething()")
                it.println("}")
                it.println()
            }
            it.println("val app = App()")
            it.println()
            it.println("class ${kotlinClass.simpleName} {")
            it.println("    private var visited = false")
            it.println()
            it.println("    // public method referenced by other classes")
            it.println("    fun doSomething() {")
            it.println("        if (!visited) {")
            it.println("            println(\"visit ${kotlinClass.name}\")")
            it.println("            visited = true")
            it.println("        }")
            it.println("    }")
            it.println("}")
        }
    }
}