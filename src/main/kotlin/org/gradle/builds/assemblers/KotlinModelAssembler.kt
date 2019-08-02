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

        val mainClass = application.addClass("${project.qualifiedNamespaceFor}.${project.typeNameFor}")
        mainClass.addRole(AppEntryPoint())
        buildScript.property("mainClassName", "${mainClass.name}Kt")
    }

    override fun library(settings: Settings, project: Project, library: KotlinLibrary) {
        val buildScript = project.buildScript
        buildScript.requirePlugin("kotlin")

        addKotlinLibs(buildScript)

        library.addClass("${project.qualifiedNamespaceFor}.${project.typeNameFor}")
    }

    private fun addKotlinLibs(buildScript: BuildScript) {
        buildScript.dependsOnExternal("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
        buildScript.dependsOnExternal("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}