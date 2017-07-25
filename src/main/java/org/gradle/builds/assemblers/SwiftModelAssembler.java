package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildScript;
import org.gradle.builds.model.Project;
import org.gradle.builds.model.SwiftApplication;
import org.gradle.builds.model.SwiftLibrary;

public class SwiftModelAssembler extends AbstractModelAssembler {
    @Override
    protected void populate(Settings settings, Project project) {
        if (project.component(SwiftLibrary.class) != null) {
            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-module");
        } else if (project.component(SwiftApplication.class) != null) {
            BuildScript buildScript = project.getBuildScript();
            buildScript.requirePlugin("swift-executable");
        }
    }
}
