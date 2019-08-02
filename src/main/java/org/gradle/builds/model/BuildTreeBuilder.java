package org.gradle.builds.model;

import org.gradle.builds.assemblers.GitRepoBuilder;

import java.nio.file.Path;

/**
 * Allows a graph of builds to be defined.
 */
public interface BuildTreeBuilder extends BuildTree<BuildSettingsBuilder> {
    /**
     * Returns the root directory of this tree.
     */
    Path getRootDir();

    /**
     * Adds a build to this tree.
     */
    BuildSettingsBuilder addBuild(String rootDir);

    /**
     * Adds a build to this tree.
     */
    BuildSettingsBuilder addBuild(Path rootDir);

    /**
     * Returns the main repo for this model.
     */
    GitRepoBuilder getRepo();

    /**
     * Adds an additional repo.
     */
    GitRepoBuilder addRepo(Path rootDir);
}
