package org.gradle.builds.model;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Build {
    private final Path rootDir;
    private final Project rootProject;
    private final Map<String, Project> subprojects = new LinkedHashMap<>();

    public Build(Path rootDir) {
        this.rootDir = rootDir;
        rootProject = new Project(1, null, "testApp", rootDir);
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Project getRootProject() {
        return rootProject;
    }

    public Set<Project> getProjects() {
        Set<Project> projects = new LinkedHashSet<>(subprojects.size() + 1);
        projects.add(rootProject);
        projects.addAll(subprojects.values());
        return projects;
    }

    public Set<Project> getSubprojects() {
        return new LinkedHashSet<>(subprojects.values());
    }

    public Project addProject(String name) {
        Project project = new Project(subprojects.size() + 2, rootProject, name, rootDir.resolve(name));
        subprojects.put(name, project);
        return project;
    }
}
