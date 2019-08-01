package org.gradle.builds.assemblers

import org.gradle.builds.model.KotlinApplication
import org.gradle.builds.model.KotlinLibrary
import org.gradle.builds.model.Project

class KotlinBuildProjectInitializer : ProjectInitializer() {
    override fun initRootProject(project: Project) {
        project.addComponent(KotlinApplication())
    }

    override fun initLibraryProject(project: Project) {
        project.addComponent(KotlinLibrary())
    }
}
