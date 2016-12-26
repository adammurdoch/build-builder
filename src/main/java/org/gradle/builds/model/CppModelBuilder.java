package org.gradle.builds.model;

public class CppModelBuilder extends ModelBuilder {
    @Override
    public void populate(Build build) {
        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Empty) {
                continue;
            }
            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
        }
    }
}
