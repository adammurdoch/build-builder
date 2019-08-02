package org.gradle.builds.model;

import java.util.List;

public interface BuildTree<T> {
    /**
     * Returns the main build for this model.
     */
    T getMainBuild();

    /**
     * Returns all of the builds that make up this tree.
     */
    List<? extends T> getBuilds();

    /**
     * Returns all of the git repos that make up this tree.
     */
    List<? extends GitRepo> getRepos();
}
