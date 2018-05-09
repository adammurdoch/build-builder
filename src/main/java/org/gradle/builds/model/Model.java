package org.gradle.builds.model;

import java.util.List;
import java.util.stream.Collectors;

public class Model {
    private final Build build;
    private final List<Build> builds;

    public Model(Build build, List<Build> builds) {
        this.build = build;
        this.builds = builds;
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
        return builds.stream().map(b -> new GitRepo(b.getRootDir(), b.getVersion())).collect(Collectors.toList());
    }
}
