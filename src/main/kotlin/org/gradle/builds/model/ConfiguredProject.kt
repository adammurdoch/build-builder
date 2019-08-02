package org.gradle.builds.model

import java.nio.file.Path

interface ConfiguredProject {
    val projectDir: Path

    val name: String

    val buildScript: ConfiguredBuildScript

    val requiredProjects: List<Dependency<ConfiguredProject>>

    fun <T : Component> component(type: Class<T>): T?

    fun <T> requiredLibraries(type: Class<T>): List<Dependency<Library<T>>>
}
