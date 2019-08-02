package org.gradle.builds.model

import org.gradle.builds.assemblers.GitRepoBuilder

import java.nio.file.Path

/**
 * Allows a graph of builds to be defined.
 */
interface BuildTreeBuilder : BuildTree<BuildStructureBuilder> {
    /**
     * Returns the root directory of this tree.
     */
    val rootDir: Path

    /**
     * Returns the main repo for this model.
     */
    val repo: GitRepoBuilder

    /**
     * Adds a build to this tree.
     */
    fun addBuild(rootDir: String): BuildStructureBuilder

    /**
     * Adds a build to this tree.
     */
    fun addBuild(rootDir: Path): BuildStructureBuilder

    /**
     * Adds an additional repo.
     */
    fun addRepo(rootDir: Path): GitRepoBuilder
}
