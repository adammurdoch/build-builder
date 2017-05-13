package org.gradle.builds.assemblers;

import org.gradle.builds.model.AndroidApplication;
import org.gradle.builds.model.AndroidLibrary;
import org.gradle.builds.model.JavaLibrary;
import org.gradle.builds.model.Project;

public class AndroidBuildProjectInitializer extends ProjectInitializer {
    private final boolean includeJavaLibraries;

    public AndroidBuildProjectInitializer(boolean includeJavaLibraries) {
        this.includeJavaLibraries = includeJavaLibraries;
    }

    @Override
    public void initRootProject(Project project) {
        project.addComponent(new AndroidApplication());
    }

    @Override
    public void initLibraryProject(Project project) {
        project.addComponent(new AndroidLibrary(project.getName()));
    }

    @Override
    public void initAlternateLibraryProject(Project project) {
        if (includeJavaLibraries) {
            JavaLibrary javaLibrary = new JavaLibrary(project.getName());
            javaLibrary.setTargetJavaVersion("1.7");
            project.addComponent(javaLibrary);
        } else {
            initLibraryProject(project);
        }
    }
}
