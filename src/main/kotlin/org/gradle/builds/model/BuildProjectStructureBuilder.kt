package org.gradle.builds.model

import org.gradle.builds.assemblers.ProjectInitializer
import org.gradle.builds.assemblers.Settings

import java.nio.file.Path

/**
 * A build whose relationships to other builds are read-only, and whose project structure is mutable.
 */
interface BuildProjectStructureBuilder : Build {
    val rootDir: Path

    val rootProject: Project

    val projects: Set<Project>

    val subprojects: Set<Project>

    val projectInitializer: ProjectInitializer

    val settings: Settings

    val exportedLibraries: List<PublishedLibrary<*>>

    val publicationTarget: PublicationTarget

    val dependsOn: List<BuildProjectStructureBuilder>

    val typeNamePrefix: String

    val includedBuilds: List<BuildProjectStructureBuilder>

    val name: String

    var deepestProject: Project

    val version: String

    val sourceBuilds: List<BuildProjectStructureBuilder>

    fun addProject(name: String): Project

    fun addProject(path: String, projectDir: Path): Project

    fun exportProject(project: Project)
}
