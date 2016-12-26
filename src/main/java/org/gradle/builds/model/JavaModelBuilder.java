package org.gradle.builds.model;

public class JavaModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        BuildScript buildScript = build.getRootProject().getBuildScript();
        buildScript.requirePlugin("java");
    }
}
