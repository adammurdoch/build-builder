package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class SwiftModelAssembler extends AbstractModelAssembler {
    @Override
    protected void rootProject(Project rootProject) {
        rootProject.getBuildScript().allProjects().requirePlugin("xcode");
    }

    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(SwiftLibrary.class) != null) {
            SwiftLibrary library = project.component(SwiftLibrary.class);

            SwiftClass apiClass = new SwiftClass(classNameFor(project));
            library.setApiClass(apiClass);
            library.setModule(capitalize(project.getName()));

            SwiftSourceFile apiSourceFile = library.addSourceFile(fileNameFor(project) + ".swift");
            apiSourceFile.addClass(apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-library");
            buildScript.requirePlugin("xctest");
            addPublishing(project, library, project.getBuildScript());
            addDependencies(project, library, buildScript, true);
            if (library.isSwiftPm()) {
                buildScript.block("library").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
            }

            addSource(project, library, apiClass, apiSourceFile);
            addTests(project, library);
        } else if (project.component(SwiftApplication.class) != null) {
            SwiftApplication application = project.component(SwiftApplication.class);

            SwiftClass appClass = new SwiftClass(classNameFor(project));

            SwiftSourceFile mainSourceFile = application.addSourceFile("main.swift");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addClass(appClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-executable");
            buildScript.requirePlugin("xctest");
            addDependencies(project, application, buildScript, false);
            if (application.isSwiftPm()) {
                buildScript.block("executable").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
            }

            addSource(project, application, appClass, mainSourceFile);
            addTests(project, application);
        }
    }

    private void addPublishing(Project project, SwiftLibrary library, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String module = project.getName();
            String version = "1.2";
            project.addComponent(new PublishedLibrary<>(new ExternalDependencyDeclaration(group, module, version), library.getApi()));
            buildScript.property("group", group);
            buildScript.property("version", version);
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
                addReferences(component, swiftClass, swiftSourceFile);
            }
            for (SwiftClass dep : dependencies) {
                swiftClass.uses(dep);
            }
            return swiftClass;
        });
    }

    private void addReferences(HasSwiftSource component, SwiftClass swiftClass, SwiftSourceFile sourceFile) {
        for (SwiftLibraryApi library : component.getReferencedLibraries()) {
            swiftClass.uses(library.getApiClass());
            sourceFile.addModule(library.getModule());
        }
    }

    private void addDependencies(Project project, HasSwiftSource component, BuildScript buildScript, boolean libHack) {
        // TODO - remove this hack
        buildScript.block("configurations").statement("testImplementation.extendsFrom(implementation)");
        // TODO - remove this hack
        String configuration = libHack ? "api" : "implementation";
        for (Project dep : project.getDependencies()) {
            buildScript.dependsOn(configuration, new ProjectDependencyDeclaration(dep.getPath()));
            component.uses(dep.component(SwiftLibrary.class).getApi());
        }
        for (PublishedLibrary<? extends SwiftLibraryApi> library : project.getExternalDependencies(SwiftLibraryApi.class)) {
            buildScript.dependsOn(configuration, library.getGav());
            component.uses(library.getApi());
        }
    }

    private void addTests(Project project, HasSwiftSource application) {
        for (SwiftSourceFile sourceFile : application.getSourceFiles()) {
            for (SwiftClass swiftClass : sourceFile.getClasses()) {
                String className = swiftClass.getName() + "Test";
                SwiftSourceFile testSourceFile = application.addTestFile(new SwiftSourceFile(className.toLowerCase() + ".swift"));
                testSourceFile.addClass(new SwiftClass(className));
            }
        }
    }
}
