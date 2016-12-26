package org.gradle.builds.model;

public class AndroidModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        Project rootProject = build.getRootProject();

        AndroidApplication androidApplication = rootProject.addComponent(new AndroidApplication());
        androidApplication.setPackageName("org.gradle.example");

        BuildScript buildScript = rootProject.getBuildScript();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.2.2");
        buildScript.requirePlugin("com.android.application");
        ScriptBlock androidBlock = buildScript.block("android");
        androidBlock.property("buildToolsVersion", "25.0.0");
        androidBlock.property("compileSdkVersion", 25);
    }
}
