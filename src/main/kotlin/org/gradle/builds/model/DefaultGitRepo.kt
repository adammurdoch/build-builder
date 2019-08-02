package org.gradle.builds.model

import java.nio.file.Path

/**
 * Represents a Git repository to be generated.
 */
class DefaultGitRepo(
        override val rootDir: Path,
        override val version: String) : GitRepo {
}