package org.gradle.builds.model;

import java.util.List;

public class DefaultBuildTree implements BuildTree<BuildProjectStructureBuilder> {
    private final BuildProjectStructureBuilder build;
    private final List<BuildProjectStructureBuilder> builds;
    private final List<GitRepo> repos;

    public DefaultBuildTree(BuildProjectStructureBuilder build, List<BuildProjectStructureBuilder> builds, List<GitRepo> repos) {
        this.build = build;
        this.builds = builds;
        this.repos = repos;
    }

    @Override
    public BuildProjectStructureBuilder getMainBuild() {
        return build;
    }

    @Override
    public List<BuildProjectStructureBuilder> getBuilds() {
        return builds;
    }

    @Override
    public List<GitRepo> getRepos() {
        return repos;
    }
}
