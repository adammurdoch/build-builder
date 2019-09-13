package org.gradle.builds.assemblers

import org.gradle.builds.model.*

class KotlinModelAssembler: LanguageSpecificProjectConfigurer<KotlinApplication, KotlinLibrary>(KotlinApplication::class.java, KotlinLibrary::class.java) {
    override fun rootProject(settings: Settings, project: Project) {
        val buildScript = project.buildScript
        buildScript.buildScriptBlock().jcenter()
        buildScript.requireOnBuildScriptClasspath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        buildScript.allProjects().jcenter()
    }

    override fun application(settings: Settings, project: Project, application: KotlinApplication) {
        val buildScript = project.buildScript
        buildScript.requirePlugin("kotlin")
        buildScript.requirePlugin("application")

        addKotlinLibs(buildScript)
        addDependencies(project, application, buildScript)

        val mainClass = application.addClass("${project.qualifiedNamespaceFor}.${project.typeNameFor}")
        mainClass.addRole(AppEntryPoint())
        buildScript.property("mainClassName", "${mainClass.name}Kt")
        addDependencies(application, mainClass)
    }

    override fun library(settings: Settings, project: Project, library: KotlinLibrary) {
        val buildScript = project.buildScript
        buildScript.requirePlugin("kotlin")

        addKotlinLibs(buildScript)
        addDependencies(project, library, buildScript)

        val apiClass = library.apiClass
        addDependencies(library, apiClass)
    }

    private fun addDependencies(component: HasKotlinSource, apiClass: KotlinClass) {
        for (incomingLibrary in component.referencedLibraries) {
            for (incomingClass in incomingLibrary.target.apiClasses) {
                apiClass.uses(incomingLibrary.withTarget(incomingClass))
            }
        }
    }

    private fun addDependencies(project: Project, component: HasKotlinSource, buildScript: BuildScript) {
        for (library in project.requiredLibraries(KotlinLibraryApi::class.java)) {
            buildScript.dependsOn("implementation", library.target.dependency)
            component.uses(library.withTarget(library.target.api))
        }
    }

    private fun addKotlinLibs(buildScript: BuildScript) {
        buildScript.dependsOnExternal("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
        buildScript.dependsOnExternal("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}