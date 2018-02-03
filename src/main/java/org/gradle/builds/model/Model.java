package org.gradle.builds.model;

import java.util.List;

public class Model {
    private final Build build;
    private final List<Build> builds;

    public Model(Build build, List<Build> builds) {
        this.build = build;
        this.builds = builds;
    }

    /**
     * Returns the main build for this model.
     */
    public Build getBuild() {
        return build;
    }

    public List<Build> getBuilds() {
        return builds;
    }
}
