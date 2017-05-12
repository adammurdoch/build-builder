package org.gradle.builds.assemblers;

import org.gradle.builds.model.NativeApplication;
import org.gradle.builds.model.NativeLibrary;
import org.gradle.builds.model.Project;

public class CppBuildProjectInitializer extends ProjectInitializer {
    @Override
    public void initRootProject(Project project) {
        project.addComponent(new NativeApplication());
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new NativeLibrary());
    }
}
