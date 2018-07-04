package org.gradle.builds.model;

import java.util.List;

public class BuildTree {
    private final Build build;
    private final List<Build> builds;
    private final List<GitRepo> repos;

    public BuildTree(Build build, List<Build> builds, List<GitRepo> repos) {
        this.build = build;
        this.builds = builds;
        this.repos = repos;
    }

    /**
     * Returns the main build.
     */
    public Build getBuild() {
        return build;
    }

    /**
     * Returns all of the builds to be generated.
     */
    public List<Build> getBuilds() {
        return builds;
    }

    /**
     * Returns the Git repositories to be generated.
     */
    public List<GitRepo> getRepos() {
        return repos;
    }
}
