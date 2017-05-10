package org.gradle.builds.model;

import java.util.*;

public class Model {
    private final Build build;
    private final List<Build> builds = new ArrayList<>();

    public Model(Build build) {
        this.build = build;
        builds.add(build);
    }

    /**
     * Returns the main build for this model.
     */
    public Build getBuild() {
        return build;
    }

    public void addBuild(Build build) {
        builds.add(build);
    }

    public List<Build> getBuilds() {
        return builds;
    }
}
