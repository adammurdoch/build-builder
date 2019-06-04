package org.gradle.builds.generators

import org.gradle.builds.model.Build
import kotlin.math.max
import kotlin.math.min

class GradlePropertiesGenerator: Generator<Build> {
    override fun generate(build: Build, fileGenerator: FileGenerator) {
        val readMe = build.rootDir.resolve("gradle.properties")
        val size = max(64, build.projects.size * 4)
        fileGenerator.generate(readMe) { writer ->
            writer.println("org.gradle.jvmargs=-Xmx${size}m")
        }
    }
}
