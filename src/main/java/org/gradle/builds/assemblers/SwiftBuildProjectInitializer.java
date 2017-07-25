package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;
import org.gradle.builds.model.SwiftApplication;
import org.gradle.builds.model.SwiftLibrary;

public class SwiftBuildProjectInitializer extends ProjectInitializer {
    @Override
    public void initRootProject(Project project) {
        project.addComponent(new SwiftApplication());
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new SwiftLibrary());
    }
}
