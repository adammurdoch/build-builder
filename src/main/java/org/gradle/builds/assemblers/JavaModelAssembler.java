package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends JvmModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.getRole() == Project.Role.Library) {
            JavaLibrary library = project.addComponent(new JavaLibrary());
            JavaClass apiClass = library.addClass(javaPackageFor(project) + "." + classNameFor(project));
            library.setApiClass(apiClass);
            addSource(project, library, apiClass, javaClass -> {});
            addTests(project, library);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addDependencies(project, buildScript);
        } else if (project.getRole() == Project.Role.Application) {
            JavaApplication application = project.addComponent(new JavaApplication());
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

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOnProject("compile", dep.getPath());
        }
        buildScript.dependsOnExternal("testCompile", "junit:junit:4.12");
    }
}
