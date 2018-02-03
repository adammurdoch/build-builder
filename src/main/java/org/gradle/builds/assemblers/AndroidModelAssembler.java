package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.Collections;

public class AndroidModelAssembler extends JvmModelAssembler {
    public static final String defaultVersion = "3.0.0";
    private static final PublishedLibrary<JavaLibraryApi> supportUtils = new PublishedLibrary<>("support-core-utils", new ExternalDependencyDeclaration("com.android.support:support-core-utils:25.1.0"), new JavaLibraryApi("support-core-utils", Collections.singletonList(JavaClassApi.field("android.support.v4.app.NavUtils", "PARENT_ACTIVITY"))));
    private final String pluginVersion;

    public AndroidModelAssembler(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void rootProject(Project rootProject) {
        super.rootProject(rootProject);
        BuildScript buildScript = rootProject.getBuildScript();
        if (pluginVersion.startsWith("2.5.")) {
            buildScript.buildScriptBlock().mavenLocal();
        }
        buildScript.buildScriptBlock().google();
        buildScript.buildScriptBlock().jcenter();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:" + pluginVersion);
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(AndroidApplication.class) != null) {
            AndroidApplication androidApplication = project.component(AndroidApplication.class);
            if (androidApplication.getPackageName() == null) {
                androidApplication.setPackageName(project.getQualifiedNamespaceFor());
            }
            project.requires(slfj4);
            project.requires(supportUtils);
            JavaClassApi rClass = JavaClassApi.field(androidApplication.getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");

            JavaClass appActivity = androidApplication.addClass(androidApplication.getPackageName() + "." + project.getTypeNameFor() + "MainActivity");
            appActivity.addRole(new AndroidActivity());
            androidApplication.activity(appActivity);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.application");
            addDependencies(project, androidApplication, buildScript);
            addApplicationResources(androidApplication);

            ScriptBlock androidBlock = buildScript.block("android");
            androidBlock.property("buildToolsVersion", "26.0.2");
            androidBlock.property("compileSdkVersion", 26);
            ScriptBlock configBlock = androidBlock.block("defaultConfig");
            configBlock.property("applicationId", androidApplication.getPackageName());
            configBlock.property("minSdkVersion", 21);
            configBlock.property("targetSdkVersion", 26);
            configBlock.property("versionCode", 1);
            configBlock.property("versionName", "1.0.0");
            configBlock.property("testInstrumentationRunner", "android.support.test.runner.AndroidJUnitRunner");

            addSourceFiles(project, androidApplication, appActivity, rClass);
            addTests(project, androidApplication);
        } else if (project.component(AndroidLibrary.class) != null) {
            AndroidLibrary androidLibrary = project.component(AndroidLibrary.class);
            if (androidLibrary.getPackageName() == null) {
                androidLibrary.setPackageName(project.getQualifiedNamespaceFor());
            }
            project.requires(slfj4);
            JavaClassApi rClass = JavaClassApi.field(androidLibrary.getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");

            JavaClass libraryActivity = androidLibrary.addClass(androidLibrary.getPackageName() + "." + project.getTypeNameFor() + "Activity");
            libraryActivity.addRole(new AndroidActivity());
            androidLibrary.setActivity(libraryActivity);
            androidLibrary.setRClass(rClass);
            androidLibrary.activity(libraryActivity);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.library");
            addPublishing(project, androidLibrary.getApi(), buildScript);
            addDependencies(project, androidLibrary, buildScript);

            ScriptBlock androidBlock = buildScript.block("android");
            androidBlock.property("buildToolsVersion", "26.0.2");
            androidBlock.property("compileSdkVersion", 26);
            ScriptBlock configBlock = androidBlock.block("defaultConfig");
            configBlock.property("minSdkVersion", 21);
            configBlock.property("targetSdkVersion", 26);
            configBlock.property("versionCode", 1);
            configBlock.property("versionName", "1.0.0");
            configBlock.property("testInstrumentationRunner", "android.support.test.runner.AndroidJUnitRunner");

            addSourceFiles(project, androidLibrary, libraryActivity, rClass);
            addTests(project, androidLibrary);
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
            String version = project.getVersion();
            project.export(new LocalLibrary<>(project, new ExternalDependencyDeclaration(group, module, version), api));
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven");
                ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
                deployerBlock.statement("repository(url: new URI('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "'))");
                buildScript.statement("task publish(dependsOn: uploadArchives)");
            }
        } else {
            project.export(new LocalLibrary<>(project, null, api));
        }
    }

    private void addDependencies(Project project, AndroidComponent component, BuildScript buildScript) {
        for (Dependency<Library<? extends JvmLibraryApi>> library : project.getRequiredLibraries(JvmLibraryApi.class)) {
            buildScript.dependsOn("compile", library.getTarget().getDependency());
            component.uses(library.withTarget(library.getTarget().getApi()));
        }
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
        buildScript.dependsOnExternal("androidTestCompile", "com.android.support:support-annotations:25.1.0");
        buildScript.dependsOnExternal("androidTestCompile", "com.android.support.test:runner:0.5");
    }

    @Override
    protected void addTests(Project project, HasJavaSource application) {
        super.addTests(project, application);
        application.addTest(project.getQualifiedNamespaceFor() + "." + project.getTypeNameFor() + "InstrumentedTest").addRole(new InstrumentedTest());
    }

    private void addSourceFiles(Project project, AndroidComponent androidComponent, JavaClass activity, JavaClassApi rClass) {
        String stringResourceName = project.getName().toLowerCase() + "_string";
        androidComponent.stringResource(stringResourceName, "some-value");

        addSource(project, androidComponent, activity, javaClass -> {
            javaClass.uses(Dependency.implementation(rClass));
        });
    }
}
