package org.gradle.builds.model;

public class AndroidModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        build.getRootProject().requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.2.2");
        build.getRootProject().requirePlugin("com.android.application");
        ScriptBlock androidBlock = build.getRootProject().block("android");
        androidBlock.property("buildToolsVersion", "25.0.0");
        androidBlock.property("compileSdkVersion", "25");
    }
}
