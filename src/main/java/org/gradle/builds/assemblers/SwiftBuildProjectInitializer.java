package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;
import org.gradle.builds.model.SwiftApplication;
import org.gradle.builds.model.SwiftLibrary;

public class SwiftBuildProjectInitializer extends ProjectInitializer {
    private final boolean swiftPm;

    public SwiftBuildProjectInitializer(boolean swiftPm) {
        this.swiftPm = swiftPm;
    }

    @Override
    public void initRootProject(Project project) {
        project.addComponent(new SwiftApplication(swiftPm));
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new SwiftLibrary(swiftPm));
    }
}
