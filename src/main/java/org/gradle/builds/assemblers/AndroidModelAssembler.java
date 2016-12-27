package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelAssembler extends ModelAssembler {
    @Override
    public void populate(Build build) {
        BuildScript buildScript = build.getRootProject().getBuildScript();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.2.2");

        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Application) {
                AndroidApplication androidApplication = project.addComponent(new AndroidApplication());
                androidApplication.setPackageName(javaIdentifierFor(project));
                JavaClass mainActivity = androidApplication.addClass(androidApplication.getPackageName() + ".AppActivity");
                JavaClass implClass = androidApplication.addClass(androidApplication.getPackageName() + ".AppImpl");
                mainActivity.uses(implClass);

                buildScript = project.getBuildScript();
                buildScript.requirePlugin("com.android.application");

                ScriptBlock androidBlock = buildScript.block("android");
                androidBlock.property("buildToolsVersion", "25.0.0");
                androidBlock.property("compileSdkVersion", 25);
                ScriptBlock configBlock = androidBlock.block("defaultConfig");
                configBlock.property("applicationId", androidApplication.getPackageName());
                configBlock.property("minSdkVersion", 21);
                configBlock.property("targetSdkVersion", 25);
                configBlock.property("versionCode", 1);
                configBlock.property("versionName", "1.0");
            } else if (project.getRole() == Project.Role.Library) {
                AndroidLibrary androidLibrary = project.addComponent(new AndroidLibrary());
                androidLibrary.setPackageName(javaIdentifierFor(project));
                JavaClass libraryActivity = androidLibrary.addClass(androidLibrary.getPackageName() + ".LibraryActivity");
                JavaClass implClass = androidLibrary.addClass(androidLibrary.getPackageName() + ".LibraryImpl");
                libraryActivity.uses(implClass);

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
