package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

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
                SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeLibrarySpec");
                addDependencies(project, componentDeclaration);
            } else if (project.getRole() == Project.Role.Application) {
                HasNativeSource app = project.addComponent(new HasNativeSource());
                app.addSourceFile(project.getName() + ".cpp");

                BuildScript buildScript = project.getBuildScript();
                buildScript.requirePlugin("native-component");
                buildScript.requirePlugin("cpp-lang");
                SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeExecutableSpec");
                addDependencies(project, componentDeclaration);
            }
        }
    }

    private void addDependencies(Project project, SoftwareModelDeclaration componentDeclaration) {
        for (Project dep : project.getDependencies()) {
            componentDeclaration.dependsOn(dep.getPath());
        }
    }
}
