package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildTree;

import java.util.HashSet;
import java.util.Set;

public class ModelConfigurer<T extends Build<T>> {
    private final BuildConfigurer<T> buildConfigurer;

    public ModelConfigurer(BuildConfigurer<T> buildConfigurer) {
        this.buildConfigurer = buildConfigurer;
    }

    /**
     * Populates the model, in build dependency order.
     */
    public void populate(BuildTree<? extends T> model) {
        Set<T> seen = new HashSet<>();
        for (T build : model.getBuilds()) {
            doPopulate(build, seen);
        }
    }

    private void doPopulate(T build, Set<T> seen) {
        for (T depBuild : build.getDependsOn()) {
            doPopulate(depBuild, seen);
        }
        if (seen.add(build)) {
            buildConfigurer.populate(build);
        }
    }
}
