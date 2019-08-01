package org.gradle.builds.assemblers

import org.gradle.builds.model.KotlinApplication
import org.gradle.builds.model.KotlinLibrary
import org.gradle.builds.model.Project

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
        val mainClassName = "${project.qualifiedNamespaceFor}.${project.typeNameFor}"
        buildScript.property("mainClassName", mainClassName)
    }

    override fun library(settings: Settings, project: Project, library: KotlinLibrary) {
        val buildScript = project.buildScript
        buildScript.requirePlugin("kotlin")
    }
}