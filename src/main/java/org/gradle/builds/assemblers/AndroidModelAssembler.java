package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelAssembler extends JvmModelAssembler {
    @Override
    protected void rootProject(Project rootProject) {
        BuildScript buildScript = rootProject.getBuildScript();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.2.2");
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.getRole() == Project.Role.Application) {
            AndroidApplication androidApplication = project.addComponent(new AndroidApplication());
            androidApplication.setPackageName(javaPackageFor(project));
            JavaClass mainActivity = androidApplication.addClass(androidApplication.getPackageName() + "." + classNameFor(project));
            addSourceFiles(project, androidApplication, mainActivity);
            addTests(project, androidApplication);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.application");
            addDependencies(project, buildScript);

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
            androidLibrary.setPackageName(javaPackageFor(project));
            JavaClass libraryActivity = androidLibrary.addClass(androidLibrary.getPackageName() + "." + classNameFor(project));
            androidLibrary.setApiClass(libraryActivity);
            addSourceFiles(project, androidLibrary, libraryActivity);
            addTests(project, androidLibrary);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.library");
            addDependencies(project, buildScript);

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

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOnProject("compile", dep.getPath());
        }
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }

    private void addSourceFiles(Project project, AndroidComponent androidComponent, JavaClass activity) {
        String stringResourceName = project.getName().toLowerCase();
        androidComponent.stringResource(stringResourceName);

        addSource(project, androidComponent, activity, javaClass -> {
            javaClass.addFieldReference(androidComponent.getPackageName() + ".R.string." + stringResourceName);
        });
    }
}
