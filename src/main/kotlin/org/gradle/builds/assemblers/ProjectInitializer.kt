package org.gradle.builds.assemblers

import org.gradle.builds.model.Project

abstract class ProjectInitializer {
    abstract fun initRootProject(project: Project)

    abstract fun initLibraryProject(project: Project)

    open fun initAlternateLibraryProject(project: Project) {
        initLibraryProject(project)
    }
}