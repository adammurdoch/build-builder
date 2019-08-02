package org.gradle.builds.model;

import org.gradle.builds.assemblers.GitRepoBuilder;

import java.nio.file.Path;
import java.util.List;

/**
 * Allows a graph of builds to be defined.
 */
public interface BuildTreeBuilder {
    /**
     * Returns the root directory of this tree.
     */
    Path getRootDir();

    /**
     * Returns the main build for this model.
     */
    BuildSettingsBuilder getMainBuild();

    /**
     * Adds a build to this tree.
     */
    BuildSettingsBuilder addBuild(String rootDir);

    /**
     * Adds a build to this tree.
     */
    BuildSettingsBuilder addBuild(Path rootDir);

    /**
     * Returns the builds that make up this tree.
     */
    List<? extends BuildSettingsBuilder> getBuilds();

    /**
     * Returns the main repo for this model.
     */
    GitRepoBuilder getRepo();

    /**
     * Adds an additional repo.
     */
    GitRepoBuilder addRepo(Path rootDir);
}
