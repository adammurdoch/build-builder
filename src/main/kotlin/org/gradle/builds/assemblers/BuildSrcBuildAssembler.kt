package org.gradle.builds.assemblers

import org.gradle.builds.model.BuildTreeBuilder
import org.gradle.builds.model.GradlePluginComponent
import org.gradle.builds.model.Project


class BuildSrcBuildAssembler : BuildTreeAssembler {
    override fun attachBuilds(settings: Settings, model: BuildTreeBuilder) {
        val build = model.addBuild("buildSrc")
        build.displayName = "buildSrc"
        build.rootProjectName = "buildSrc"
        build.settings = Settings(1, 1)
        build.projectInitializer = object: ProjectInitializer() {
            override fun initRootProject(project: Project) {
                val plugin = GradlePluginComponent()
                plugin.id = "org.gradle.example.show"
                project.addComponent(plugin)
                project.setTypeName("buildsrc")
            }

            override fun initLibraryProject(project: Project) {
            }
        }
    }
}