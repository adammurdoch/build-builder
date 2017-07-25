package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class SwiftModelAssembler extends AbstractModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(SwiftLibrary.class) != null) {
            SwiftLibrary library = project.component(SwiftLibrary.class);

            SwiftClass apiClass = new SwiftClass(classNameFor(project));
            library.setApiClass(apiClass);

            SwiftSourceFile apiSourceFile = library.addSourceFile(fileNameFor(project) + ".swift");
            apiSourceFile.addClass(apiClass);

            addSource(project, library, apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-module");
        } else if (project.component(SwiftApplication.class) != null) {
            SwiftApplication application = project.component(SwiftApplication.class);

            SwiftClass appClass = new SwiftClass(classNameFor(project));

            SwiftSourceFile mainSourceFile = application.addSourceFile("main.swift");
            mainSourceFile.addMainFunction(appClass);
            mainSourceFile.addClass(appClass);

            addSource(project, application, appClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-executable");
        }
    }

    private void addSource(Project project, HasSwiftSource component, SwiftClass entryPoint) {
        int implLayer = Math.max(0, project.getClassGraph().getLayers().size() - 2);
        project.getClassGraph().visit((Graph.Visitor<SwiftClass>) (nodeDetails, dependencies) -> {
            SwiftClass swiftClass;
            int layer = nodeDetails.getLayer();
            int item = nodeDetails.getItem();
            if (layer == 0) {
                swiftClass = entryPoint;
            } else {
                String name;
                if (nodeDetails.isLastLayer()) {
                    name = "NoDeps" + (item + 1);
                } else {
                    name = "Impl" + (layer) + "_" + (item + 1);
                }
                swiftClass = new SwiftClass(entryPoint.getName() + name);
                SwiftSourceFile swiftSourceFile = component.addSourceFile(fileNameFor(project) + "_" + name.toLowerCase() + ".swift");
                swiftSourceFile.addClass(swiftClass);
            }
            if (layer == implLayer) {
//                addReferences(project, swiftClass);
            }
            for (SwiftClass dep : dependencies) {
                swiftClass.uses(dep);
            }
            return swiftClass;
        });
    }

    private void addReferences(Project project, SwiftClass swiftClass) {
        for (Project dep : project.getDependencies()) {
            swiftClass.uses(dep.component(SwiftLibrary.class).getApiClass());
        }
    }

}
