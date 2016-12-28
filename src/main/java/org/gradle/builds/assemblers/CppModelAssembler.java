package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.util.HashSet;
import java.util.Set;

public class CppModelAssembler extends ModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.getRole() == Project.Role.Library) {
            NativeLibrary lib = project.addComponent(new NativeLibrary());

            CppClass implClass = new CppClass(className(project) + "Impl");
            addReferences(project, implClass);

            CppClass apiClass = new CppClass(className(project));
            apiClass.uses(implClass);
            lib.setApiClass(apiClass);

            CppHeaderFile apiHeader = lib.addHeaderFile(fileName(project) + ".h");
            apiHeader.addClass(apiClass);
            lib.setApiHeader(apiHeader);

            CppHeaderFile implHeader = lib.addHeaderFile(fileName(project) + "_impl.h");
            implHeader.addClass(implClass);
            implHeader.addHeader(apiHeader);

            CppSourceFile apiSourceFile = lib.addSourceFile(fileName(project) + ".cpp");
            apiSourceFile.addClass(apiClass);
            apiSourceFile.addHeader(implHeader);

            CppSourceFile implSourceFile = lib.addSourceFile(fileName(project) + "_impl.cpp");
            implSourceFile.addClass(implClass);
            implSourceFile.addHeader(implHeader);
            addLibHeaders(project, implSourceFile);

            addSource(settings, project, lib, apiClass, implHeader);

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

            CppSourceFile mainSourceFile = app.addSourceFile(fileName(project) + ".cpp");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addHeader(headerFile);

            CppSourceFile implSourceFile = app.addSourceFile(fileName(project) + "_impl.cpp");
            implSourceFile.addClass(appClass);
            implSourceFile.addHeader(headerFile);
            addLibHeaders(project, implSourceFile);

            addSource(settings, project, app, appClass, headerFile);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("native-component");
            buildScript.requirePlugin("cpp-lang");
            SoftwareModelDeclaration componentDeclaration = buildScript.componentDeclaration("main", "NativeExecutableSpec");
            addDependencies(project, componentDeclaration);
        }
    }

    private void addSource(Settings settings, Project project, HasNativeSource component, CppClass apiClass, CppHeaderFile implHeader) {
        for (int i = 2; i<settings.getSourceFileCount();i++) {
            CppClass noDepsClass = new CppClass(className(project) + "NoDeps" + (i-1));
            apiClass.uses(noDepsClass);
            implHeader.addClass(noDepsClass);
            CppSourceFile noDepsSourceFile = component.addSourceFile(fileName(project) + "_nodeps" + (i-1) + ".cpp");
            noDepsSourceFile.addClass(noDepsClass);
            noDepsSourceFile.addHeader(implHeader);
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
        // Need to include transitive dependencies
        HashSet<Project> seen = new HashSet<>();
        for (Project dep : project.getDependencies()) {
            addDependencies(componentDeclaration, dep, seen);
        }
    }

    private void addDependencies(SoftwareModelDeclaration componentDeclaration, Project project, Set<Project> seen) {
        if (!seen.add(project)) {
            return;
        }
        componentDeclaration.dependsOn(project.getPath());
        for (Project dep : project.getDependencies()) {
            addDependencies(componentDeclaration, dep, seen);
        }
    }
}
