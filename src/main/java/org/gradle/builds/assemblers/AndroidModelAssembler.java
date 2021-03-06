package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.Collections;

public class AndroidModelAssembler extends JvmModelAssembler<AndroidApplication, AndroidLibrary> {
    public static final String defaultVersion = "3.4.1";
    private static final PublishedLibrary<JavaLibraryApi> supportUtils = new PublishedLibrary<>("support-core-utils", new ExternalDependencyDeclaration("com.android.support:support-core-utils:25.1.0"), new JavaLibraryApi("support-core-utils", Collections.singletonList(JavaClassApi.field("android.support.v4.app.NavUtils", "PARENT_ACTIVITY"))));
    private final String pluginVersion;

    public AndroidModelAssembler(String pluginVersion) {
        super(AndroidApplication.class, AndroidLibrary.class);
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void rootProject(Settings settings, Project rootProject) {
        super.rootProject(settings, rootProject);
        BuildScript buildScript = rootProject.getBuildScript();
        if (pluginVersion.startsWith("2.5.")) {
            buildScript.buildScriptBlock().mavenLocal();
        }
        buildScript.buildScriptBlock().google();
        buildScript.buildScriptBlock().jcenter();
        buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:" + pluginVersion);
        buildScript.allProjects().google();
    }

    @Override
    protected void application(Settings settings, Project project, AndroidApplication androidApplication) {
        project.requires(slfj4);
        project.requires(slfj4Simple);
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
    }

    @Override
    protected void library(Settings settings, Project project, AndroidLibrary androidLibrary) {
        project.requires(slfj4);
        JavaClassApi rClass = JavaClassApi.field(androidLibrary.getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");

        JavaClass libraryActivity = androidLibrary.getActivity();

        BuildScript buildScript = project.getBuildScript();
        buildScript.requirePlugin("com.android.library");
        addPublishing(project, buildScript);
        addDependencies(project, androidLibrary, buildScript);

        ScriptBlock androidBlock = buildScript.block("android");
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

    private void addApplicationResources(AndroidApplication application) {
        String labelResource = "app_label";
        application.stringResource(labelResource, "Test App");
        application.setLabelResource(labelResource);
    }

    private void addPublishing(Project project, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String version = project.getVersion();
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven");
                ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
                deployerBlock.statement("repository(url: new URI('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "'))");
                buildScript.statement("task publish(dependsOn: uploadArchives)");
            }
        }
    }

    private void addDependencies(Project project, AndroidComponent component, BuildScript buildScript) {
        for (Dependency<Library<? extends JvmLibraryApi>> library : project.requiredLibraries(JvmLibraryApi.class)) {
            if (library.isApi()) {
                buildScript.dependsOn("api", library.getTarget().getDependency());
            } else {
                buildScript.dependsOn("implementation", library.getTarget().getDependency());
            }
            component.uses(library.withTarget(library.getTarget().getApi()));
        }
        buildScript.dependsOnExternal("testImplementation", "junit:junit:4.12");
        buildScript.dependsOnExternal("androidTestImplementation", "com.android.support:support-annotations:25.1.0");
        buildScript.dependsOnExternal("androidTestImplementation", "com.android.support.test:runner:0.5");
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
