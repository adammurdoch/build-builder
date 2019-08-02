package org.gradle.builds.model

import org.gradle.builds.assemblers.ProjectInitializer
import org.gradle.builds.assemblers.Settings

import java.nio.file.Path

/**
 * A build whose relationships to other builds are read-only, and whose project structure is mutable.
 */
interface BuildProjectStructureBuilder : Build<BuildProjectStructureBuilder> {
    val rootProject: Project

    /**
     * All projects of this build, including the root project.
     */
    val projects: Set<@JvmWildcard Project>

    val subprojects: Set<@JvmWildcard Project>

    val projectInitializer: ProjectInitializer

    val settings: Settings

    val exportedLibraries: List<PublishedLibrary<*>>

    val publicationTarget: PublicationTarget?

    val typeNamePrefix: String

    val name: String

    var deepestProject: Project

    val version: String

    fun addProject(name: String): Project

    fun addProject(path: String, projectDir: Path): Project

    fun exportProject(project: Project)
}
