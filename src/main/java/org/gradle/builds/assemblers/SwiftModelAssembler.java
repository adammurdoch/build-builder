package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class SwiftModelAssembler extends LanguageSpecificProjectConfigurer<SwiftApplication, SwiftLibrary> {
    public SwiftModelAssembler() {
        super(SwiftApplication.class, SwiftLibrary.class);
    }

    @Override
    protected void rootProject(Settings settings, Project rootProject) {
        rootProject.getBuildScript().requirePlugin("swiftpm-export", "4.6");
        addIdePlugins(rootProject);
    }

    @Override
    protected void application(Settings settings, Project project, SwiftApplication application) {
        SwiftClass appClass = new SwiftClass(project.getTypeNameFor());

        SwiftSourceFile mainSourceFile = application.addSourceFile("main.swift");
        mainSourceFile.addMainFunction(appClass);
        mainSourceFile.addClass(appClass);

        BuildScript buildScript = project.getBuildScript();
        buildScript.requirePlugin("swift-executable", "4.2", "4.4");
        buildScript.requirePlugin("swift-application", "4.5");
        buildScript.requirePlugin("xctest");
        addDependencies(project, application, buildScript);
        if (application.isSwiftPm()) {
            buildScript.block("application").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
        }

        addSource(project, application, appClass, mainSourceFile);
        addTests(application);
    }

    @Override
    protected void library(Settings settings, Project project, SwiftLibrary library) {
        SwiftClass apiClass = library.getApiClass();

        SwiftSourceFile apiSourceFile = library.addSourceFile(apiClass.getName() + ".swift");
        apiSourceFile.addClass(apiClass);

        BuildScript buildScript = project.getBuildScript();
        buildScript.requirePlugin("swift-library");
        buildScript.requirePlugin("xctest");
        addPublishing(project, project.getBuildScript());
        addDependencies(project, library, buildScript);
        if (library.isSwiftPm()) {
            buildScript.block("library").property("source.from", new Scope.Code("rootProject.file('Sources/" + project.getName() + "')"));
        }

        addSource(project, library, apiClass, apiSourceFile);
        addTests(library);
    }

    private void addPublishing(Project project, BuildScript buildScript) {
        if (project.getPublicationTarget() != null) {
            String group = "org.gradle.example";
            String version = project.getVersion();
            buildScript.property("group", group);
            buildScript.property("version", version);
        }
    }

    private void addSource(Project project, HasSwiftSource component, SwiftClass entryPoint, SwiftSourceFile sourceFile) {
        project.getClassGraph().visit((Graph.Visitor<SwiftClass>) (nodeDetails, dependencies) -> {
            SwiftClass swiftClass;
            SwiftSourceFile swiftSourceFile;
            int layer = nodeDetails.getLayer();
            if (layer == 0) {
                swiftClass = entryPoint;
                swiftSourceFile = sourceFile;
            } else {
                swiftClass = new SwiftClass(entryPoint.getName() + "Impl" + nodeDetails.getNameSuffix());
                swiftSourceFile = component.addSourceFile(swiftClass.getName() + ".swift");
                swiftSourceFile.addClass(swiftClass);
            }
            if (nodeDetails.isReceiveIncoming()) {
                addReferences(component, swiftClass, swiftSourceFile);
            }
            for (Dependency<SwiftClass> dep : dependencies) {
                swiftClass.uses(dep);
            }
            return swiftClass;
        });
    }

    private void addReferences(HasSwiftSource component, SwiftClass swiftClass, SwiftSourceFile sourceFile) {
        for (Dependency<SwiftLibraryApi> dependency : component.getReferencedLibraries()) {
            SwiftLibraryApi api = dependency.getTarget();
            swiftClass.uses(dependency.withTarget(api.getApiClass()));
            sourceFile.addModule(api.getModule());
        }
    }

    private void addDependencies(Project project, HasSwiftSource component, BuildScript buildScript) {
        // TODO - remove this hack
        String configuration = component instanceof SwiftLibrary ? "api" : "implementation";
        for (Dependency<Library<? extends SwiftLibraryApi>> library : project.requiredLibraries(SwiftLibraryApi.class)) {
            buildScript.dependsOn(configuration, library.getTarget().getDependency());
            component.uses(library.withTarget(library.getTarget().getApi()));
        }
    }

    private void addTests(HasSwiftSource component) {
        for (SwiftSourceFile sourceFile : component.getSourceFiles()) {
            for (SwiftClass swiftClass : sourceFile.getClasses()) {
                String className = swiftClass.getName() + "Test";
                SwiftSourceFile testSourceFile = component.addTestFile(new SwiftSourceFile(className + ".swift"));
                testSourceFile.addModule(component.getModule());
                SwiftClass testClass = new SwiftClass(className);
                testClass.addRole(new XCUnitTest(swiftClass));
                testSourceFile.addClass(testClass);
            }
        }
    }
}
