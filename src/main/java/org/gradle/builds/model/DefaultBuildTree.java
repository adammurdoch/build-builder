package org.gradle.builds.model;

import java.util.List;

public class DefaultBuildTree implements BuildTree<BuildProjectTreeBuilder> {
    private final BuildProjectTreeBuilder build;
    private final List<BuildProjectTreeBuilder> builds;
    private final List<GitRepo> repos;

    public DefaultBuildTree(BuildProjectTreeBuilder build, List<BuildProjectTreeBuilder> builds, List<GitRepo> repos) {
        this.build = build;
        this.builds = builds;
        this.repos = repos;
    }

    @Override
    public BuildProjectTreeBuilder getMainBuild() {
        return build;
    }

    @Override
    public List<BuildProjectTreeBuilder> getBuilds() {
        return builds;
    }

    @Override
    public List<GitRepo> getRepos() {
        return repos;
    }
}
