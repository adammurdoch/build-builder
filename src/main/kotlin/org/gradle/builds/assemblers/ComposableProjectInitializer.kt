package org.gradle.builds.assemblers

import org.gradle.builds.model.Project
import java.util.function.Consumer


class ComposableProjectInitializer : ProjectInitializer() {
    private val rootProjectMutators = ArrayList<Consumer<in Project>>()
    private val libraryProjectMutators = ArrayList<Consumer<in Project>>()
    private val alternativeLibraryProjectMutators = ArrayList<Consumer<in Project>>()

    fun rootProject(action: (Project) -> Unit) {
        rootProjectMutators.add(Consumer { project -> action(project) })
    }

    fun rootProject(action: Consumer<in Project>) {
        rootProjectMutators.add(action)
    }

    fun add(initializer: ProjectInitializer) {
        rootProjectMutators.add(Consumer { project -> initializer.initRootProject(project) })
        libraryProjectMutators.add(Consumer { project -> initializer.initLibraryProject(project) })
        alternativeLibraryProjectMutators.add(Consumer { project -> initializer.initAlternateLibraryProject(project) })
    }

    override fun initRootProject(project: Project) {
        rootProjectMutators.forEach { it.accept(project) }
    }

    override fun initLibraryProject(project: Project) {
        libraryProjectMutators.forEach { it.accept(project) }
    }

    override fun initAlternateLibraryProject(project: Project) {
        alternativeLibraryProjectMutators.forEach { it.accept(project) }
    }
}