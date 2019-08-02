package org.gradle.builds.model

import java.nio.file.Path

/**
 * Represents a Git repository to be generated.
 */
interface GitRepo {
    /**
     * The repo dir.
     */
    val rootDir: Path
    /**
     * The initial version of the repo.
     */
    val version: String
}