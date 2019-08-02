package org.gradle.builds.model

import org.gradle.builds.assemblers.Settings
import java.nio.file.Path

class DefaultConfiguredBuild(
        override val name: String,
        override val rootDir: Path,
        override val settings: Settings,
        override val rootProject: ConfiguredProject,
        override val projects: Set<ConfiguredProject>,
        override val subprojects: Set<ConfiguredProject>,
        override val deepestProject: ConfiguredProject,
        override val dependsOn: Set<ConfiguredBuild>,
        override val includedBuilds: Set<ConfiguredBuild>,
        override val sourceBuilds: Set<ConfiguredBuild>
): ConfiguredBuild