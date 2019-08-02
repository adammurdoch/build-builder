package org.gradle.builds.model;

import java.util.List;

public class DefaultBuildTree implements BuildTree {
    private final Build build;
    private final List<Build> builds;
    private final List<GitRepo> repos;

    public DefaultBuildTree(Build build, List<Build> builds, List<GitRepo> repos) {
        this.build = build;
        this.builds = builds;
        this.repos = repos;
    }

    @Override
    public Build getBuild() {
        return build;
    }

    @Override
    public List<Build> getBuilds() {
        return builds;
    }

    @Override
    public List<GitRepo> getRepos() {
        return repos;
    }
}
