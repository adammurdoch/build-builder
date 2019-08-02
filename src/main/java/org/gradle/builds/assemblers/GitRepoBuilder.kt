package org.gradle.builds.assemblers

import org.gradle.builds.model.GitRepo
import java.nio.file.Path


class GitRepoBuilder(override val rootDir: Path): GitRepo {
    override var version: String = "1.0.0"
}
