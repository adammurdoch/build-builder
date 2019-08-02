package org.gradle.builds.model

interface BuildTree<T> {
    /**
     * Returns the main build for this model.
     */
    val mainBuild: T

    /**
     * Returns all of the builds that make up this tree.
     */
    val builds: List<@JvmWildcard T>

    /**
     * Returns all of the git repos that make up this tree.
     */
    val repos: List<@JvmWildcard GitRepo>
}
