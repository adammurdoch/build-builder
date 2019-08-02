package org.gradle.builds.model;

import java.util.List;

public abstract class AbstractBuildTree<T> implements BuildTree<T> {
    private final T build;
    private final List<T> builds;
    private final List<GitRepo> repos;

    public AbstractBuildTree(T build, List<T> builds, List<GitRepo> repos) {
        this.build = build;
        this.builds = builds;
        this.repos = repos;
    }

    @Override
    public T getMainBuild() {
        return build;
    }

    @Override
    public List<T> getBuilds() {
        return builds;
    }

    @Override
    public List<GitRepo> getRepos() {
        return repos;
    }
}
