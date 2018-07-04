package org.gradle.builds.assemblers

import org.gradle.builds.model.Project


interface ProjectConfigurer {
    /**
     * Called after dependencies have been configured.
     */
    fun configure(settings: Settings, project: Project)
}