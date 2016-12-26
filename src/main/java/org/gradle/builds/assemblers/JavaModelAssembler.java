package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class JavaModelAssembler extends ModelAssembler {
    @Override
    public void populate(Build build) {
        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Library) {
                JavaLibrary library = project.addComponent(new JavaLibrary());
                library.addClass(identifierFor(project) + ".Library");

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
            } else if (project.getRole() == Project.Role.Application) {
                JavaApplication application = project.addComponent(new JavaApplication());
                JavaClass appClass = application.addClass(identifierFor(project) + ".App");

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
                buildScript.requirePlugin("application");
                buildScript.property("mainClassName", appClass.getName());
            }
        }
    }
}
