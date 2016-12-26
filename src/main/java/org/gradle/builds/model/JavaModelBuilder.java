package org.gradle.builds.model;

public class JavaModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Library) {
                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
            } else if (project.getRole() == Project.Role.Application) {
                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("java");
                buildScript.requirePlugin("application");
                buildScript.property("mainClassName", "org.gradle.example.App");
            }
        }
    }
}
