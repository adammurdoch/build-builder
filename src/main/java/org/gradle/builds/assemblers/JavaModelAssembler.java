package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends JvmModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(JavaLibrary.class) != null) {
            JavaLibrary library = project.component(JavaLibrary.class);

            project.dependsOn(slfj4);

            JavaClass apiClass = library.addClass(javaPackageFor(project) + "." + classNameFor(project));
            library.setApiClass(apiClass);
            addSource(project, library, apiClass, javaClass -> {});
            addTests(project, library);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addPublishing(project, apiClass, buildScript);
            addDependencies(project, buildScript);
            addJavaVersion(library, buildScript);
        } else if (project.component(JavaApplication.class) != null) {
            JavaApplication application = project.component(JavaApplication.class);

            project.dependsOn(slfj4);

            JavaClass mainClass = application.addClass(javaPackageFor(project) + "." + classNameFor(project));
            mainClass.addRole(new AppEntryPoint());
            addSource(project, application, mainClass, javaClass -> {});
            addTests(project, application);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            buildScript.requirePlugin("application");
            addDependencies(project, buildScript);
            buildScript.property("mainClassName", mainClass.getName());
        }
    }

    private void addPublishing(Project project, JavaClass apiClass, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.addComponent(new PublishedJvmLibrary(new ExternalDependencyDeclaration(group, module, version), apiClass.getApi()));
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven");
                ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
                deployerBlock.statement("repository(url: new URI('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "'))");
            }
        }
    }

    private void addJavaVersion(JavaLibrary library, BuildScript buildScript) {
        if (library.getTargetJavaVersion() != null) {
            buildScript.property("sourceCompatibility", library.getTargetJavaVersion());
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
}
