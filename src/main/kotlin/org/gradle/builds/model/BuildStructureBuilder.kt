package org.gradle.builds.model

import org.gradle.builds.assemblers.ComposableProjectInitializer
import org.gradle.builds.assemblers.Settings

/**
 * A build whose relationships to other builds are mutable.
 */
interface BuildStructureBuilder : Build<BuildStructureBuilder> {
    var displayName: String

    var rootProjectName: String

    var settings: Settings

    val projectInitializer: ComposableProjectInitializer

    var typeNamePrefix: String

    var version: String

    fun publishAs(publicationTarget: PublicationTarget)

    fun sourceDependency(childBuild: BuildStructureBuilder)

    fun dependsOn(childBuild: BuildStructureBuilder)

    fun includeBuild(childBuild: BuildStructureBuilder)
}
