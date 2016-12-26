package org.gradle.builds.model;

public class CppModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        BuildScript buildScript = build.getRootProject().getBuildScript();
        buildScript.requirePlugin("native-component");
        buildScript.requirePlugin("cpp-lang");
    }
}
