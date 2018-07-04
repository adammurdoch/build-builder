package org.gradle.builds.assemblers

import org.gradle.builds.model.Component
import org.gradle.builds.model.Project


abstract class ComponentSpecificProjectConfigurer<T : Component>(val type: Class<T>) : ProjectConfigurer {
    override fun configure(settings: Settings, project: Project) {
        val component = project.component(type)
        if (component != null) {
            configure(settings, project, component)
        }
    }

    abstract fun configure(settings: Settings, project: Project, component: T)
}