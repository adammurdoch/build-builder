package org.gradle.builds.model;

public class AndroidModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        build.getRootProject().requirePlugin("com.android.application");
    }
}
