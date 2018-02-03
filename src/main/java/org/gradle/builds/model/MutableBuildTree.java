package org.gradle.builds.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows a graph of builds to be defined.
 */
public class MutableBuildTree {
    private final Build build;
    private final List<Build> builds = new ArrayList<>();

    public MutableBuildTree(Build build) {
        this.build = build;
        builds.add(build);
    }

    public Path getRootDir() {
        return build.getRootDir();
    }

    /**
     * Returns the main build for this model.
     */
    public Build getMainBuild() {
        return build;
    }

    public void addBuild(Build build) {
        builds.add(build);
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public Model toModel() {
        return new Model(build, builds);
    }
}
