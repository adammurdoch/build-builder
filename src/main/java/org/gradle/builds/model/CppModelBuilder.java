package org.gradle.builds.model;

public class CppModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        build.getRootProject().requirePlugin("native-component");
        build.getRootProject().requirePlugin("cpp-lang");
    }
}
