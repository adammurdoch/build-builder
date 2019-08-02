package org.gradle.builds.model

import org.gradle.builds.assemblers.Settings

interface ConfiguredBuild : Build<ConfiguredBuild> {
    val name: String

    val settings: Settings

    val rootProject: ConfiguredProject

    val deepestProject: ConfiguredProject

    val projects: Set<@JvmWildcard ConfiguredProject>

    val subprojects: Set<@JvmWildcard ConfiguredProject>
}