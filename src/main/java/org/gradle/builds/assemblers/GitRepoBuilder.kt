package org.gradle.builds.assemblers

import java.nio.file.Path


class GitRepoBuilder(val rootDir: Path) {
    var version: String = "1.0.0"
}
