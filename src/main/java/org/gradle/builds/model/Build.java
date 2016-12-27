package org.gradle.builds.model;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Build {
    private final Path rootDir;
    private final Project rootProject;
    private final Map<String, Project> subprojects = new TreeMap<>();

    public Build(Path rootDir) {
        this.rootDir = rootDir;
        rootProject = new Project(null, "testApp", rootDir);
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
        Project project = new Project(rootProject, name, rootDir.resolve(name));
        subprojects.put(name, project);
        return project;
    }
}
