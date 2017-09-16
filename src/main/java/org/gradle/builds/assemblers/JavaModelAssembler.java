package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends JvmModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(JavaLibrary.class) != null) {
            JavaLibrary library = project.component(JavaLibrary.class);

            project.requires(slfj4);

            JavaClass apiClass = library.addClass(javaPackageFor(project) + "." + classNameFor(project));
            library.setApiClass(apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addPublishing(project, library.getApi(), buildScript);
            addDependencies(project, library, buildScript);
            addJavaVersion(library, buildScript);

            addSource(project, library, apiClass, javaClass -> {});
            addTests(project, library);
        } else if (project.component(JavaApplication.class) != null) {
            JavaApplication application = project.component(JavaApplication.class);

            project.requires(slfj4);

            JavaClass mainClass = application.addClass(javaPackageFor(project) + "." + classNameFor(project));
            mainClass.addRole(new AppEntryPoint());

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            buildScript.requirePlugin("application");
            addDependencies(project, application, buildScript);
            buildScript.property("mainClassName", mainClass.getName());

            addSource(project, application, mainClass, javaClass -> {});
            addTests(project, application);
        }
    }

    private void addPublishing(Project project, JavaLibraryApi api, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.export(new LocalLibrary<>(project, new ExternalDependencyDeclaration(group, module, version), api));
            buildScript.property("group", group);
            buildScript.property("version", version);
            if (project.getPublicationTarget().getHttpRepository() != null) {
                buildScript.requirePlugin("maven");
                ScriptBlock deployerBlock = buildScript.block("uploadArchives").block("repositories").block("mavenDeployer");
                deployerBlock.statement("repository(url: new URI('" + project.getPublicationTarget().getHttpRepository().getRootDir().toUri() + "'))");
            }
        } else {
            project.export(new LocalLibrary<>(project, null, api));
        }
    }

    private void addJavaVersion(JavaLibrary library, BuildScript buildScript) {
        if (library.getTargetJavaVersion() != null) {
            buildScript.property("sourceCompatibility", library.getTargetJavaVersion());
        }
    }

    private void addDependencies(Project project, HasJavaSource<JavaLibraryApi> component, BuildScript buildScript) {
        // Don't use Android libraries, only java libraries
        for (Library<? extends JavaLibraryApi> library : project.getRequiredLibraries(JavaLibraryApi.class)) {
            buildScript.dependsOn("compile", library.getDependency());
            component.uses(library.getApi());
        }

        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }
}
