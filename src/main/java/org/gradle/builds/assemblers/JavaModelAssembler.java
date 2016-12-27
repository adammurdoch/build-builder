package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends ModelAssembler {
    @Override
    public void populate(Build build) {
        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Library) {
                JavaLibrary library = project.addComponent(new JavaLibrary());
                JavaClass apiClass = library.addClass(javaIdentifierFor(project) + ".Library");
                JavaClass implClass = library.addClass(javaIdentifierFor(project) + ".LibraryImpl");
                apiClass.uses(implClass);

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
            } else if (project.getRole() == Project.Role.Application) {
                JavaApplication application = project.addComponent(new JavaApplication());
                JavaClass mainClass = application.addClass(javaIdentifierFor(project) + ".App");
                mainClass.addMainMethod();
                JavaClass implClass = application.addClass(javaIdentifierFor(project) + ".AppImpl");
                mainClass.uses(implClass);

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
                buildScript.requirePlugin("application");
                buildScript.property("mainClassName", mainClass.getName());
            }
        }
    }
}
