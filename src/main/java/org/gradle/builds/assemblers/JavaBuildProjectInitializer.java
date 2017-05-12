package org.gradle.builds.assemblers;

import org.gradle.builds.model.JavaApplication;
import org.gradle.builds.model.JavaLibrary;
import org.gradle.builds.model.Project;

public class JavaBuildProjectInitializer extends ProjectInitializer {
    @Override
    public void initRootProject(Project project) {
        project.addComponent(new JavaApplication());
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new JavaLibrary(project.getName()));
    }
}
