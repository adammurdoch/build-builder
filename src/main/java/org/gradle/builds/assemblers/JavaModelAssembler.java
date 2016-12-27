package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends ModelAssembler {
    @Override
    protected void populate(Project project) {
        if (project.getRole() == Project.Role.Library) {
            JavaLibrary library = project.addComponent(new JavaLibrary());
            JavaClass apiClass = library.addClass(javaIdentifierFor(project) + ".Library");
            library.setApiClass(apiClass);
            addSource(project, library, apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            addDependencies(project, buildScript);
        } else if (project.getRole() == Project.Role.Application) {
            JavaApplication application = project.addComponent(new JavaApplication());
            JavaClass mainClass = application.addClass(javaIdentifierFor(project) + ".App");
            mainClass.addMainMethod();
            addSource(project, application, mainClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("java");
            buildScript.requirePlugin("application");
            addDependencies(project, buildScript);
            buildScript.property("mainClassName", mainClass.getName());
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependency("compile", dep.getPath());
        }
    }

    private void addSource(Project project, HasJavaSource library, JavaClass apiClass) {
        JavaClass implClass = library.addClass(apiClass.getName() + "Impl");
        apiClass.uses(implClass);
        for (Project depProject : project.getDependencies()) {
            implClass.uses(depProject.component(JvmLibrary.class).getApiClass());
        }
    }
}
