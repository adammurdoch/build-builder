package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

class LibraryRootProjectInitializer extends ProjectInitializer {
    private final String rootProjectTypeName;
    private final ProjectInitializer initializer;

    public LibraryRootProjectInitializer(String rootProjectTypeName, ProjectInitializer initializer) {
        this.rootProjectTypeName = rootProjectTypeName;
        this.initializer = initializer;
    }

    @Override
    public void initRootProject(Project project) {
        project.setTypeName(rootProjectTypeName);
        initializer.initLibraryProject(project);
    }

    @Override
    public void initLibraryProject(Project project) {
        initializer.initLibraryProject(project);
    }

    @Override
    public void initAlternateLibraryProject(Project project) {
        initializer.initAlternateLibraryProject(project);
    }
}
