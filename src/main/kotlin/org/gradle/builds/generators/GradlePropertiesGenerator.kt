package org.gradle.builds.generators

import org.gradle.builds.model.ConfiguredBuild
import org.gradle.builds.model.HasHeapRequirements
import kotlin.math.max

class GradlePropertiesGenerator : Generator<ConfiguredBuild> {
    override fun generate(build: ConfiguredBuild, fileGenerator: FileGenerator) {
        val readMe = build.rootDir.resolve("gradle.properties")
        val component = build.rootProject.component(HasHeapRequirements::class.java)
        val min = component?.minHeapMegabytes ?: 64
        val size = max(min, build.projects.size * 4)
        fileGenerator.generate(readMe) { writer ->
            writer.println("org.gradle.jvmargs=-Xmx${size}m")
        }
    }
}
