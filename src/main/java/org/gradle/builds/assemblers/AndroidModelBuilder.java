package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        BuildScript buildScript = build.getRootProject().getBuildScript();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.2.2");

        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Application) {
                AndroidApplication androidApplication = project.addComponent(new AndroidApplication());
                androidApplication.setPackageName("org.gradle.example");

                buildScript = project.getBuildScript();
                buildScript.requirePlugin("com.android.application");

                ScriptBlock androidBlock = buildScript.block("android");
                androidBlock.property("buildToolsVersion", "25.0.0");
                androidBlock.property("compileSdkVersion", 25);
                ScriptBlock configBlock = androidBlock.block("defaultConfig");
                configBlock.property("applicationId", "org.gradle.example");
                configBlock.property("minSdkVersion", 21);
                configBlock.property("targetSdkVersion", 25);
                configBlock.property("versionCode", 1);
                configBlock.property("versionName", "1.0");
            } else if (project.getRole() == Project.Role.Library) {
                AndroidLibrary androidApplication = project.addComponent(new AndroidLibrary());
                androidApplication.setPackageName("org.gradle.example");

                buildScript = project.getBuildScript();
                buildScript.requirePlugin("com.android.library");

                ScriptBlock androidBlock = buildScript.block("android");
                androidBlock.property("buildToolsVersion", "25.0.0");
                androidBlock.property("compileSdkVersion", 25);
                ScriptBlock configBlock = androidBlock.block("defaultConfig");
                configBlock.property("minSdkVersion", 21);
                configBlock.property("targetSdkVersion", 25);
                configBlock.property("versionCode", 1);
                configBlock.property("versionName", "1.0");
            }
        }
    }
}
