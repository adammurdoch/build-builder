package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class CppModelAssembler extends AbstractModelAssembler {
    @Override
    protected void rootProject(Project rootProject) {
        ProjectScriptBlock allProjects = rootProject.getBuildScript().allProjects();
        allProjects.requirePlugin("xcode");
        allProjects.requirePlugin("maven-publish");
        allProjects.property("group", "test");
        allProjects.property("version", "1.2");
        allProjects.block("publishing")
                .block("repositories")
                .block("maven")
                .property("url", new Scope.Code("rootProject.file('repo')"));
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(CppLibrary.class) != null) {
            CppLibrary lib = project.component(CppLibrary.class);

            CppClass apiClass = new CppClass(classNameFor(project));
            lib.setApiClass(apiClass);

            CppHeaderFile apiHeader = lib.addPublicHeaderFile(fileNameFor(project) + ".h");
            apiHeader.addClass(apiClass);
            lib.setApiHeader(apiHeader);

            CppHeaderFile implHeader = lib.addImplementationHeaderFile(fileNameFor(project) + "_impl.h");
            implHeader.addHeader(apiHeader);

            CppSourceFile apiSourceFile = lib.addSourceFile(fileNameFor(project) + ".cpp");
            apiSourceFile.addClass(apiClass);
            apiSourceFile.addHeader(implHeader);

            addSource(project, lib, apiClass, apiSourceFile, implHeader);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-library");
            addDependencies(project, buildScript);
        } else if (project.component(CppApplication.class) != null) {
            CppApplication app = project.component(CppApplication.class);

            CppClass appClass = new CppClass(classNameFor(project));

            CppHeaderFile headerFile = app.addImplementationHeaderFile(fileNameFor(project) + ".h");
            headerFile.addClass(appClass);

            CppSourceFile mainSourceFile = app.addSourceFile(fileNameFor(project) + ".cpp");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addHeader(headerFile);
            mainSourceFile.addClass(appClass);

            addSource(project, app, appClass, mainSourceFile, headerFile);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("cpp-executable");
            addDependencies(project, buildScript);
        }
    }

    private void addSource(Project project, HasCppSource component, CppClass entryPoint, CppSourceFile entryPointSourceFile, CppHeaderFile implHeader) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<CppClass>) (nodeDetails, dependencies) -> {
            CppClass cppClass;
            CppSourceFile cppSourceFile;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                cppClass = entryPoint;
                cppSourceFile = entryPointSourceFile;
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                cppClass = new CppClass(entryPoint.getName() + name);
                implHeader.addClass(cppClass);
                cppSourceFile = component.addSourceFile(fileNameFor(project) + "_" + name.toLowerCase() + ".cpp");
                cppSourceFile.addClass(cppClass);
                cppSourceFile.addHeader(implHeader);
            }
            if (layer == implLayer) {
                addReferences(project, cppClass);
                addLibHeaders(project, cppSourceFile);
            }
            for (CppClass dep : dependencies) {
                cppClass.uses(dep);
            }
            return cppClass;
        });
    }

    private void addLibHeaders(Project project, CppSourceFile sourceFile) {
        for (Project dep : project.getDependencies()) {
            sourceFile.addHeader(dep.component(CppLibrary.class).getApiHeader());
        }
    }

    private void addReferences(Project project, CppClass cppClass) {
        for (Project dep : project.getDependencies()) {
            cppClass.uses(dep.component(CppLibrary.class).getApiClass());
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOn("implementation", new ProjectDependencyDeclaration(dep.getPath()));
        }
    }
}
