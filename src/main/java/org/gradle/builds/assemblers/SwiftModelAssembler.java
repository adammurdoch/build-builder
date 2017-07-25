package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class SwiftModelAssembler extends AbstractModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(SwiftLibrary.class) != null) {
            SwiftLibrary library = project.component(SwiftLibrary.class);
            SwiftClass apiClass = new SwiftClass(classNameFor(project));

            SwiftSourceFile apiSourceFile = library.addSourceFile(fileNameFor(project) + ".swift");
            apiSourceFile.addClass(apiClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-module");
        } else if (project.component(SwiftApplication.class) != null) {
            SwiftApplication application = project.component(SwiftApplication.class);
            SwiftClass appClass = new SwiftClass(classNameFor(project));

            SwiftSourceFile mainSourceFile = application.addSourceFile("main.swift");
            mainSourceFile.addClass(appClass);

            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-executable");
        }
    }
}
