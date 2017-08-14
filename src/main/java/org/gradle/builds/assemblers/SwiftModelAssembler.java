package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class SwiftModelAssembler extends AbstractModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(SwiftLibrary.class) != null) {
            SwiftLibrary library = project.component(SwiftLibrary.class);

            SwiftClass apiClass = new SwiftClass(classNameFor(project));
            library.setApiClass(apiClass);
            library.setModule(project.getName());

            SwiftSourceFile apiSourceFile = library.addSourceFile(fileNameFor(project) + ".swift");
            apiSourceFile.addClass(apiClass);

            addSource(project, library, apiClass, apiSourceFile);
            addTests(project, library);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-library");
            addDependencies(project, buildScript);
            if (library.isSwiftPm()) {
                buildScript.block("library").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
            }
        } else if (project.component(SwiftApplication.class) != null) {
            SwiftApplication application = project.component(SwiftApplication.class);

            SwiftClass appClass = new SwiftClass(classNameFor(project));

            SwiftSourceFile mainSourceFile = application.addSourceFile("main.swift");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addClass(appClass);

            addSource(project, application, appClass, mainSourceFile);
            addTests(project, application);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-executable");
            addDependencies(project, buildScript);
            if (application.isSwiftPm()) {
                buildScript.block("executable").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
            }
        }
    }

    private void addSource(Project project, HasSwiftSource component, SwiftClass entryPoint, SwiftSourceFile sourceFile) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<SwiftClass>) (nodeDetails, dependencies) -> {
            SwiftClass swiftClass;
            SwiftSourceFile swiftSourceFile;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                swiftClass = entryPoint;
                swiftSourceFile = sourceFile;
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                swiftClass = new SwiftClass(entryPoint.getName() + name);
                swiftSourceFile = component.addSourceFile(fileNameFor(project) + "_" + name.toLowerCase() + ".swift");
                swiftSourceFile.addClass(swiftClass);
            }
            if (layer == implLayer) {
                addReferences(project, swiftClass, swiftSourceFile);
            }
            for (SwiftClass dep : dependencies) {
                swiftClass.uses(dep);
            }
            return swiftClass;
        });
    }

    private void addReferences(Project project, SwiftClass swiftClass, SwiftSourceFile sourceFile) {
        for (Project dep : project.getDependencies()) {
            SwiftLibrary library = dep.component(SwiftLibrary.class);
            swiftClass.uses(library.getApiClass());
            sourceFile.addModule(library.getModule());
        }
    }

    private void addDependencies(Project project, BuildScript buildScript) {
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOn("implementation", new ProjectDependencyDeclaration(dep.getPath()));
        }
    }

    private void addTests(Project project, HasSwiftSource application) {
        for (SwiftSourceFile sourceFile : application.getSourceFiles()) {
            for (SwiftClass swiftClass : sourceFile.getClasses()) {
                String className = swiftClass.getName() + "Test";
                SwiftSourceFile testSourceFile = application.addTestFile(new SwiftSourceFile(className.toLowerCase() +  ".swift"));
                testSourceFile.addClass(new SwiftClass(className));
            }
        }
    }

}
