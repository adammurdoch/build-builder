package org.gradle.builds.assemblers;

import org.gradle.builds.model.CppApplication;
import org.gradle.builds.model.CppLibrary;
import org.gradle.builds.model.Project;

public class CppBuildProjectInitializer extends ProjectInitializer {
    @Override
    public void initRootProject(Project project) {
        project.addComponent(new CppApplication());
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new CppLibrary());
    }
}
