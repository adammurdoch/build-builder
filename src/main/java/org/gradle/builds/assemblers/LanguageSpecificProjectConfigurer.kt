package org.gradle.builds.assemblers

import org.gradle.builds.model.Component
import org.gradle.builds.model.Project


abstract class LanguageSpecificProjectConfigurer<A : Component, L : Component>(val applicationType: Class<A>, val libraryType: Class<L>) : ProjectConfigurer {
    override fun configure(settings: Settings, project: Project) {
        if (project.parent == null) {
            rootProject(settings, project)
        }
        val application = project.component(applicationType)
        if (application != null) {
            application(settings, project, application)
            return
        }
        val library = project.component(libraryType)
        if (library != null) {
            library(settings, project, library)
        }
    }

    protected abstract fun rootProject(settings: Settings, project: Project)

    protected abstract fun application(settings: Settings, project: Project, application: A)

    protected abstract fun library(settings: Settings, project: Project, library: L)

    protected fun addIdePlugins(rootProject: Project) {
        val allProjects = rootProject.buildScript.allProjects()
        allProjects.requirePlugin("idea")
        allProjects.requirePlugin("eclipse")
        allProjects.requirePlugin("xcode", "4.2")
        allProjects.requirePlugin("visual-studio")
    }

    protected fun capitalize(s: String): String {
        return s.capitalize()
    }
}