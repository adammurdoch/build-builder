package org.gradle.builds.model

import java.nio.file.Path

/**
 * A read-only build.
 */
interface Build<T> {
    val rootDir: Path

    val dependsOn: Set<@JvmWildcard T>

    val includedBuilds: Set<@JvmWildcard T>

    val sourceBuilds: Set<@JvmWildcard T>
}
