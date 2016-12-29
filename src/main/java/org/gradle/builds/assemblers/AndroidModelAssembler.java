package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelAssembler extends ModelAssembler {
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
            addSourceFiles(settings, project, androidApplication, mainActivity);

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
            addSourceFiles(settings, project, androidLibrary, libraryActivity);

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
            buildScript.dependency("compile", dep.getPath());
        }
    }

    private void addSourceFiles(Settings settings, Project project, AndroidComponent androidComponent, JavaClass activity) {
        JavaClass implClass = androidComponent.addClass(activity.getName() + "Impl");
        activity.uses(implClass);
        for (Project depProject : project.getDependencies()) {
            implClass.uses(depProject.component(JvmLibrary.class).getApiClass());
        }
        for (int i = 2; i < settings.getSourceFileCount(); i++) {
            JavaClass noDepsClass = androidComponent.addClass(activity.getName() + "NoDeps" + (i-1));
            activity.uses(noDepsClass);
        }
    }
}
