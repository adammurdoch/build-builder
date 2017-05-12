package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelAssembler extends JvmModelAssembler {
    public static final String defaultVersion = "2.3.1";
    private static final PublishedJvmLibrary supportUtils = new PublishedJavaLibrary(new ExternalDependencyDeclaration("com.android.support:support-core-utils:25.1.0"), JavaClassApi.field("android.support.v4.app.NavUtils", "PARENT_ACTIVITY"));
    private final String pluginVersion;

    public AndroidModelAssembler(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void rootProject(Project rootProject) {
        super.rootProject(rootProject);
        BuildScript buildScript = rootProject.getBuildScript();
        if (pluginVersion.startsWith("2.5.")) {
            buildScript.useMavenLocalForBuildScriptClasspath();
        }
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:" + pluginVersion);
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(AndroidApplication.class) != null) {
            AndroidApplication androidApplication = project.component(AndroidApplication.class);
            if (androidApplication.getPackageName() == null) {
                androidApplication.setPackageName(javaPackageFor(project));
            }
            project.dependsOn(slfj4);
            project.dependsOn(supportUtils);
            JavaClassApi rClass = JavaClassApi.field(androidApplication.getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");

            JavaClass appActivity = androidApplication.addClass(androidApplication.getPackageName() + "." + classNameFor(project) + "MainActivity");
            appActivity.addRole(new AndroidActivity());
            androidApplication.activity(appActivity);
            addSourceFiles(project, androidApplication, appActivity, rClass);
            addTests(project, androidApplication);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.application");
            addDependencies(project, buildScript);
            addApplicationResources(androidApplication);

            ScriptBlock androidBlock = buildScript.block("android");
            androidBlock.property("buildToolsVersion", "25.0.0");
            androidBlock.property("compileSdkVersion", 25);
            ScriptBlock configBlock = androidBlock.block("defaultConfig");
            configBlock.property("applicationId", androidApplication.getPackageName());
            configBlock.property("minSdkVersion", 21);
            configBlock.property("targetSdkVersion", 25);
            configBlock.property("versionCode", 1);
            configBlock.property("versionName", "1.0");
        } else if (project.component(AndroidLibrary.class) != null) {
            AndroidLibrary androidLibrary = project.component(AndroidLibrary.class);
            if (androidLibrary.getPackageName() == null) {
                androidLibrary.setPackageName(javaPackageFor(project));
            }
            project.dependsOn(slfj4);
            JavaClassApi rClass = JavaClassApi.field(androidLibrary.getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");

            JavaClass libraryActivity = androidLibrary.addClass(androidLibrary.getPackageName() + "." + classNameFor(project) + "Activity");
            libraryActivity.addRole(new AndroidActivity());
            androidLibrary.setActivity(libraryActivity);
            androidLibrary.setRClass(rClass);
            androidLibrary.activity(libraryActivity);
            addSourceFiles(project, androidLibrary, libraryActivity, rClass);
            addTests(project, androidLibrary);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.library");
            addPublishing(project, androidLibrary.getApi(), buildScript);
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

    private void addApplicationResources(AndroidApplication application) {
        String labelResource = "app_label";
        application.stringResource(labelResource, "Test App");
        application.setLabelResource(labelResource);
    }

    private void addPublishing(Project project, AndroidLibraryApi api, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.addComponent(new PublishedAndroidLibrary(new ExternalDependencyDeclaration(group, module, version), api));
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven");
                ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
                deployerBlock.statement("repository(url: new URI('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "'))");
            }
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOnProject("compile", dep.getPath());
        }
        for (PublishedJvmLibrary library : project.getExternalDependencies()) {
            buildScript.dependsOn("compile", library.getGav());
        }
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }

    private void addSourceFiles(Project project, AndroidComponent androidComponent, JavaClass activity, JavaClassApi rClass) {
        String stringResourceName = project.getName().toLowerCase() + "_string";
        androidComponent.stringResource(stringResourceName, "some-value");

        addSource(project, androidComponent, activity, javaClass -> {
            javaClass.uses(rClass);
        });
    }
}
