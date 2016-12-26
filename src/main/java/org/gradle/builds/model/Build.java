package org.gradle.builds.model;

import java.nio.file.Path;

public class Build {
    private final Path rootDir;
    private final Project rootProject = new Project();

    public Build(Path rootDir) {
        this.rootDir = rootDir;
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Project getRootProject() {
        return rootProject;
    }
}
