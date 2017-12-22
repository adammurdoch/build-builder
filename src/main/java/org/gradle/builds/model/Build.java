package org.gradle.builds.model;

import org.gradle.builds.assemblers.ProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Build {
    private final Path rootDir;
    private final String displayName;
    private final Project rootProject;
    private final Map<String, Project> projects = new LinkedHashMap<>();
    private Settings settings;
    private final List<Build> dependsOn = new ArrayList<>();
    private final List<Build> includedBuilds = new ArrayList<>();
    private final List<Build> sourceBuilds = new ArrayList<>();
    private final List<PublishedLibrary<?>> publishedLibraries = new ArrayList<>();
    private PublicationTarget publicationTarget;
    private String typeNamePrefix = "";
    private ProjectInitializer projectInitializer;
    private Project deepestProject;
    private String version = "1.0";

    public Build(Path rootDir, String displayName, String rootProjectName) {
        this.rootDir = rootDir;
        this.displayName = displayName;
        rootProject = new Project(null, rootProjectName, rootDir);
        projects.put(rootProject.getPath(), rootProject);
    }

    @Override
    public String toString() {
        return displayName;
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Project getRootProject() {
        return rootProject;
    }

    public Set<Project> getProjects() {
        Set<Project> projects = new LinkedHashSet<>(this.projects.size() + 1);
        projects.add(rootProject);
        projects.addAll(this.projects.values());
        return projects;
    }

    public Set<Project> getSubprojects() {
        return new LinkedHashSet<>(projects.values().stream().filter(project -> project != rootProject).collect(Collectors.toList()));
    }

    public Project addProject(String name) {
        return addProject(name, rootProject, rootDir.resolve(name));
    }

    public Project addProject(String path, Path projectDir) {
        if (!path.startsWith(":")) {
            throw new IllegalArgumentException("Invalid path specified: " + path);
        }
        int pos = path.lastIndexOf(':');
        String parentPath = pos == 0 ? ":" : path.substring(0, pos);
        String name = path.substring(pos + 1);
        return addProject(name, getOrCreate(parentPath), projectDir);
    }

    private Project getOrCreate(String path) {
        if (path.equals(":")) {
            return rootProject;
        }

        Project project = projects.get(path);
        if (project != null) {
            return project;
        }
        return addProject(path, rootDir.resolve(path.replace(':', '/').substring(1)));
    }

    private Project addProject(String name, Project parent, Path projectDir) {
        Project project = new Project(parent, name, projectDir);
        if (projects.containsKey(project.getPath())) {
            throw new IllegalArgumentException("Project " + project.getPath() + " already exists.");
        }
        projects.put(project.getPath(), project);
        return project;
    }

    public ProjectInitializer getProjectInitializer() {
        return projectInitializer;
    }

    public void setProjectInitializer(ProjectInitializer projectInitializer) {
        this.projectInitializer = projectInitializer;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public List<PublishedLibrary<?>> getPublishedLibraries() {
        return publishedLibraries;
    }

    public void publishLibrary(PublishedLibrary<?> library) {
        publishedLibraries.add(library);
    }

    public PublicationTarget getPublicationTarget() {
        return publicationTarget;
    }

    public void publishAs(PublicationTarget publicationTarget) {
        this.publicationTarget = publicationTarget;
    }

    public List<Build> getDependsOn() {
        return dependsOn;
    }

    public void dependsOn(Build build) {
        this.dependsOn.add(build);
    }

    public void setTypeNamePrefix(String projectNamePrefix) {
        this.typeNamePrefix = projectNamePrefix;
    }

    public String getTypeNamePrefix() {
        return typeNamePrefix;
    }

    public List<Build> getIncludedBuilds() {
        return includedBuilds;
    }

    public void includeBuild(Build build) {
        includedBuilds.add(build);
    }

    public String getName() {
        return rootProject.getName();
    }

    public void setDeepestProject(Project deepestProject) {
        this.deepestProject = deepestProject;
    }

    public Project getDeepestProject() {
        return deepestProject;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public List<Build> getSourceBuilds() {
        return sourceBuilds;
    }

    public void sourceDependency(Build build) {
        sourceBuilds.add(build);
    }
}
