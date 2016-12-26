package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.ModelBuilder;
import org.gradle.builds.model.Project;

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
