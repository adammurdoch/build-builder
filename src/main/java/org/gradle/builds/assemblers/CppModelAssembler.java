package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class CppModelAssembler extends ModelAssembler {
    @Override
    protected void populate(Project project) {
        if (project.getRole() == Project.Role.Library) {
            NativeLibrary lib = project.addComponent(new NativeLibrary());

            CppClass libClass = new CppClass(className(project));
            lib.setApiClass(libClass);
            addReferences(project, libClass);

            CppHeaderFile headerFile = lib.addHeaderFile(fileName(project) + ".h");
            headerFile.addClass(libClass);
            lib.setApiHeader(headerFile);

            CppSourceFile sourceFile = lib.addSourceFile(fileName(project) + ".cpp");
            sourceFile.addClass(libClass);
            sourceFile.addHeader(headerFile);
            addLibHeaders(project, sourceFile);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
            SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeLibrarySpec");
            addDependencies(project, componentDeclaration);
        } else if (project.getRole() == Project.Role.Application) {
            NativeApplication app = project.addComponent(new NativeApplication());

            CppClass appClass = new CppClass(className(project));
            addReferences(project, appClass);

            CppHeaderFile headerFile = app.addHeaderFile(fileName(project) + ".h");
            headerFile.addClass(appClass);

            CppSourceFile sourceFile = app.addSourceFile(fileName(project) + ".cpp");
            sourceFile.addMainFunction();
            sourceFile.addClass(appClass);
            sourceFile.addHeader(headerFile);
            addLibHeaders(project, sourceFile);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
            SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeExecutableSpec");
            addDependencies(project, componentDeclaration);
        }
    }

    private void addLibHeaders(Project project, CppSourceFile sourceFile) {
        for (Project dep : project.getDependencies()) {
            sourceFile.addHeader(dep.component(NativeLibrary.class).getApiHeader());
        }
    }

    private void addReferences(Project project, CppClass cppClass) {
        for (Project dep : project.getDependencies()) {
            cppClass.uses(dep.component(NativeLibrary.class).getApiClass());
        }
    }

    private String fileName(Project project) {
        return project.getName();
    }

    private String className(Project project) {
        return project.getName();
    }

    private void addDependencies(Project project, SoftwareModelDeclaration componentDeclaration) {
        for (Project dep : project.getDependencies()) {
            componentDeclaration.dependsOn(dep.getPath());
        }
    }
}
