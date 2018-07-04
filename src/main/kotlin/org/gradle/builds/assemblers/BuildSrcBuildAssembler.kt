package org.gradle.builds.assemblers

import org.gradle.builds.model.BuildTreeBuilder
import org.gradle.builds.model.GradlePluginComponent


class BuildSrcBuildAssembler : BuildTreeAssembler {
    override fun attachBuilds(settings: Settings, model: BuildTreeBuilder) {
        val plugin = GradlePluginComponent()
        plugin.id = "org.gradle.example.show"

        val build = model.addBuild("buildSrc")
        build.displayName = "buildSrc"
        build.rootProjectName = "buildSrc"
        build.settings = Settings(1, 1)
        build.projectInitializer.rootProject { project ->
            project.addComponent(plugin)
            project.setTypeName("buildsrc")
        }
        model.mainBuild.projectInitializer.rootProject { project ->
            project.buildScript.requirePlugin(plugin.id)
        }
    }
}