package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends JvmModelAssembler {
    @Override
    public void apply(Class<? extends Component> component, Project project) {
        if (component.equals(JavaLibrary.class) || component.equals(Library.class)) {
            project.addComponent(new JavaLibrary());
        } else if (component.equals(JavaApplication.class) || component.equals(Application.class)) {
            project.addComponent(new JavaApplication());
        }
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(JavaLibrary.class) != null) {
            JavaLibrary library = project.component(JavaLibrary.class);
            JavaClass apiClass = library.addClass(javaPackageFor(project) + "." + classNameFor(project));
            library.setApiClass(apiClass);
            addSource(project, library, apiClass, javaClass -> {});
            addTests(project, library);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addPublishing(project, buildScript);
            addDependencies(project, buildScript);
            addJavaVersion(library, buildScript);
        } else if (project.component(JavaApplication.class) != null) {
            JavaApplication application = project.component(JavaApplication.class);
            JavaClass mainClass = application.addClass(javaPackageFor(project) + "." + classNameFor(project));
            mainClass.addRole(new AppEntryPoint());
            addSource(project, application, mainClass, javaClass -> {});
            addTests(project, application);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            buildScript.requirePlugin("application");
            addPublishing(project, buildScript);
            addDependencies(project, buildScript);
            buildScript.property("mainClassName", mainClass.getName());
        }
    }

    private void addPublishing(Project project, BuildScript buildScript) {
        if (project.getPublishGroup() != null) {
            buildScript.requirePlugin("maven-publish");
            buildScript.property("group", project.getPublishGroup());
            ScriptBlock publishing = buildScript.block("publishing");
            publishing.block("repositories").block("maven").property("url", new Scope.Code("rootProject.file('build/repo')"));
            ScriptBlock publication = publishing.block("publications").block("maven(MavenPublication)");
            publication.statement("from components.java");
            publication.property("groupId", project.getPublishGroup());
            publication.property("artifactId", project.getPublishModule());
            publication.property("version", "1.2");
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
        buildScript.dependsOnExternal("compile", "org.slf4j:slf4j-api:1.7.25");
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }
}
