package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.HasNativeSource;
import org.gradle.builds.model.Project;
import org.gradle.builds.model.SoftwareModelDeclaration;

public class CppModelAssembler extends ModelAssembler {
    @Override
    protected void populate(Project project) {
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

    private void addDependencies(Project project, SoftwareModelDeclaration componentDeclaration) {
        for (Project dep : project.getDependencies()) {
            componentDeclaration.dependsOn(dep.getPath());
        }
    }
}
