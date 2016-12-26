package org.gradle.builds.model;

public class JavaModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        build.getRootProject().requirePlugin("java");
    }
}
