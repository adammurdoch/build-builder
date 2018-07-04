package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildTree;

import java.util.HashSet;
import java.util.Set;

public class ModelConfigurer {
    private final BuildConfigurer buildConfigurer;

    public ModelConfigurer(BuildConfigurer buildConfigurer) {
        this.buildConfigurer = buildConfigurer;
    }

    /**
     * Populates the model. The builds and their dependencies have been defined, but no projects have been configured.
     */
    public void populate(BuildTree model) {
        Set<Build> seen = new HashSet<>();
        for (Build build : model.getBuilds()) {
            doPopulate(build, seen);
        }
    }

    private void doPopulate(Build build, Set<Build> seen) {
        for (Build depBuild : build.getDependsOn()) {
            doPopulate(depBuild, seen);
        }
        if (seen.add(build)) {
            System.out.println("* Configure " + build);
            buildConfigurer.populate(build);
        }
    }
}
