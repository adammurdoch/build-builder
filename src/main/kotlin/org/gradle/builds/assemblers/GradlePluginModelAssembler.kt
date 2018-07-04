package org.gradle.builds.assemblers

import org.gradle.builds.model.GradlePluginComponent
import org.gradle.builds.model.JavaClass
import org.gradle.builds.model.Project


class GradlePluginModelAssembler : ProjectConfigurer {
    override fun configure(settings: Settings, project: Project) {
        val plugin = project.component(GradlePluginComponent::class.java)
        if (plugin == null) {
            return
        }

        val buildScript = project.buildScript
        buildScript.requirePlugin("java-gradle-plugin")
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12")

        val id = plugin.id!!
        val pos = id.lastIndexOf(".")
        val baseName = id.substring(pos + 1)
        val impl = project.qualifiedNamespaceFor + '.' + baseName.capitalize() + "Plugin"
        plugin.implClass = JavaClass(impl)

        val block = buildScript.block("gradlePlugin").block("plugins").block(baseName)
        block.property("id", id)
        block.property("implementationClass", impl)
    }
}
