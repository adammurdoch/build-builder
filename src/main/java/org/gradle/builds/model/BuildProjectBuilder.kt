package org.gradle.builds.model

import org.gradle.builds.assemblers.Settings

/**
 * A build whose relationships to other builds and project structure are read only.
 */
interface BuildProjectBuilder: Build<BuildProjectBuilder> {
    val settings: Settings

    val projects: Set<@JvmWildcard Project>
}