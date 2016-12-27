package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.HasNativeSource;
import org.gradle.builds.model.Project;

public class CppModelAssembler extends ModelAssembler {
    @Override
    public void populate(Build build) {
        for (Project project : build.getProjects()) {
            if (project.getRole() == Project.Role.Library) {
                HasNativeSource lib = project.addComponent(new HasNativeSource());
                lib.addHeaderFile(project.getName() + ".h");
                lib.addSourceFile(project.getName() + ".cpp");

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("native-component");
                buildScript.requirePlugin("cpp-lang");
                buildScript.componentDeclaration("main", "NativeLibrarySpec");
            } else if (project.getRole() == Project.Role.Application) {
                HasNativeSource app = project.addComponent(new HasNativeSource());
                app.addSourceFile(project.getName() + ".cpp");

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("native-component");
                buildScript.requirePlugin("cpp-lang");
                buildScript.componentDeclaration("main", "NativeExecutableSpec");
            }
        }
    }
}
