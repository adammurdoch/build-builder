package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class AndroidModelAssembler extends JvmModelAssembler {
    private final boolean experimentalAndroid;
    private final boolean includeJavaLibraries;
    private final JavaModelAssembler javaModelAssembler;

    public AndroidModelAssembler(boolean experimentalAndroid, boolean includeJavaLibraries) {
        this.experimentalAndroid = experimentalAndroid;
        this.includeJavaLibraries = includeJavaLibraries;
        javaModelAssembler = new JavaModelAssembler();
    }

    @Override
    public void apply(Class<? extends Component> component, Project project) {
        if (component.equals(AndroidLibrary.class) || component.equals(Library.class)) {
            if (project.isMayUseOtherLanguage() && includeJavaLibraries) {
                javaModelAssembler.apply(component, project);
                project.component(JavaLibrary.class).setTargetJavaVersion("1.7");
            } else {
                project.addComponent(new AndroidLibrary());
            }
        } else if (component.equals(AndroidApplication.class) || component.equals(Application.class)) {
            project.addComponent(new AndroidApplication());
        }
    }

    @Override
    protected void rootProject(Project rootProject) {
        super.rootProject(rootProject);
        BuildScript buildScript = rootProject.getBuildScript();
        if (experimentalAndroid) {
            buildScript.useMavenLocalForBuildScriptClasspath();
            buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.5.0-dev");
        } else {
            buildScript.requireOnBuildScriptClasspath("com.android.tools.build:gradle:2.3.1");
        }
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.isMayUseOtherLanguage() && includeJavaLibraries) {
            javaModelAssembler.populate(settings, project);
            return;
        }

        if (project.component(AndroidApplication.class) != null) {
            AndroidApplication androidApplication = project.component(AndroidApplication.class);
            if (androidApplication.getPackageName() == null) {
                androidApplication.setPackageName(javaPackageFor(project));
            }

            JavaClass appActivity = androidApplication.addClass(androidApplication.getPackageName() + "." + classNameFor(project));
            androidApplication.activity(appActivity);
            addSourceFiles(project, androidApplication, appActivity);
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

            JavaClass libraryActivity = androidLibrary.addClass(androidLibrary.getPackageName() + "." + classNameFor(project));
            androidLibrary.setApiClass(libraryActivity);
            androidLibrary.activity(libraryActivity);
            addSourceFiles(project, androidLibrary, libraryActivity);
            addTests(project, androidLibrary);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("com.android.library");
            addPublishing(project, libraryActivity, buildScript);
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

    private void addPublishing(Project project, JavaClass libraryActivity, BuildScript buildScript) {
        if (project.getPublishRepository() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.addComponent(new PublishedJvmLibrary(new ExternalDependencyDeclaration(group, module, version), libraryActivity));
            buildScript.requirePlugin("maven");
            buildScript.property("group", group);
            buildScript.property("version", version);
            ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
            deployerBlock.statement("repository(url: new URI('" + project.getPublishRepository().getRootDir().toUri() + "'))");
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOnProject("compile", dep.getPath());
        }
        for (PublishedJvmLibrary library : project.getExternalDependencies()) {
            buildScript.dependsOn("compile", library.getGav());
        }
        buildScript.dependsOnExternal("compile", "com.android.support:support-core-utils:25.1.0");
        buildScript.dependsOnExternal("compile", "org.slf4j:slf4j-api:1.7.25");
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }

    private void addSourceFiles(Project project, AndroidComponent androidComponent, JavaClass activity) {
        String stringResourceName = project.getName().toLowerCase() + "_string";
        androidComponent.stringResource(stringResourceName, "some-value");

        addSource(project, androidComponent, activity, javaClass -> {
            javaClass.addFieldReference(androidComponent.getPackageName() + ".R.string." + stringResourceName);
        });
    }
}
