package org.gradle.builds.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Model {
    private final Build build;
    private Build repoBuild;

    public Model(Build build) {
        this.build = build;
    }

    public Build getBuild() {
        return build;
    }

    public void setRepoBuild(Build repoBuild) {
        this.repoBuild = repoBuild;
    }

    public Build getRepoBuild() {
        return repoBuild;
    }

    public List<Build> getBuilds() {
        if (repoBuild == null) {
            return Collections.singletonList(build);
        }
        return Arrays.asList(build, repoBuild);
    }
}
