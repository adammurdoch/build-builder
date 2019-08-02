package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildProjectStructureBuilder;
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
    public void populate(BuildTree<BuildProjectStructureBuilder> model) {
        Set<BuildProjectStructureBuilder> seen = new HashSet<>();
        for (BuildProjectStructureBuilder build : model.getBuilds()) {
            doPopulate(build, seen);
        }
    }

    private void doPopulate(BuildProjectStructureBuilder build, Set<BuildProjectStructureBuilder> seen) {
        for (BuildProjectStructureBuilder depBuild : build.getDependsOn()) {
            doPopulate(depBuild, seen);
        }
        if (seen.add(build)) {
            System.out.println("* Configure " + build);
            buildConfigurer.populate(build);
        }
    }
}
