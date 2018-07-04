package org.gradle.builds.assemblers

import org.gradle.builds.model.BuildTreeBuilder

/**
 * Attaches builds and settings to the given build tree.
 */
interface BuildTreeAssembler {
    fun attachBuilds(settings: Settings, model: BuildTreeBuilder)
}
