package org.gradle.builds.model;

import org.gradle.builds.assemblers.ProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultBuildProjectTreeBuilder implements BuildProjectTreeBuilder {
    private final Path rootDir;
    private final String displayName;
    private final Project rootProject;
    private final Map<String, Project> projects = new LinkedHashMap<>();
    private final List<BuildProjectTreeBuilder> dependsOn;
    private final List<BuildProjectTreeBuilder> includedBuilds;
    private final List<BuildProjectTreeBuilder> sourceBuilds;
    private final List<Project> exportedProjects = new ArrayList<>();
    private final Settings settings;
    private final PublicationTarget publicationTarget;
    private final String typeNamePrefix;
    private final ProjectInitializer projectInitializer;
    private final String version;
    private Project deepestProject;

    public DefaultBuildProjectTreeBuilder(Path rootDir, String displayName, String rootProjectName, Settings settings, PublicationTarget publicationTarget, String typeNamePrefix, ProjectInitializer projectInitializer, String version, List<BuildProjectTreeBuilder> dependsOn, List<BuildProjectTreeBuilder> includedBuilds, List<BuildProjectTreeBuilder> sourceBuilds) {
        this.rootDir = rootDir;
        this.displayName = displayName;
        this.settings = settings;
        this.publicationTarget = publicationTarget;
        this.typeNamePrefix = typeNamePrefix;
        this.projectInitializer = projectInitializer;
        this.version = version;
        this.dependsOn = dependsOn;
        this.includedBuilds = includedBuilds;
        this.sourceBuilds = sourceBuilds;
        rootProject = new Project(null, rootProjectName, rootDir);
        projects.put(rootProject.getPath(), rootProject);
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public Project getRootProject() {
        return rootProject;
    }

    @Override
    public Set<Project> getProjects() {
        Set<Project> projects = new LinkedHashSet<>(this.projects.size() + 1);
        projects.add(rootProject);
        projects.addAll(this.projects.values());
        return projects;
    }

    @Override
    public Set<Project> getSubprojects() {
        return new LinkedHashSet<>(projects.values().stream().filter(project -> project != rootProject).collect(Collectors.toList()));
    }

    @Override
    public Project addProject(String name) {
        return addProject(name, rootProject, rootDir.resolve(name));
    }

    @Override
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

    @Override
    public ProjectInitializer getProjectInitializer() {
        return projectInitializer;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public List<PublishedLibrary<?>> getExportedLibraries() {
        return exportedProjects.stream().flatMap(project -> project.getPublishedLibraries().stream()).collect(Collectors.toList());
    }

    @Override
    public PublicationTarget getPublicationTarget() {
        return publicationTarget;
    }

    @Override
    public List<BuildProjectTreeBuilder> getDependsOn() {
        return dependsOn;
    }

    @Override
    public String getTypeNamePrefix() {
        return typeNamePrefix;
    }

    @Override
    public List<BuildProjectTreeBuilder> getIncludedBuilds() {
        return includedBuilds;
    }

    @Override
    public String getName() {
        return rootProject.getName();
    }

    @Override
    public void setDeepestProject(Project deepestProject) {
        this.deepestProject = deepestProject;
    }

    @Override
    public Project getDeepestProject() {
        return deepestProject;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<BuildProjectTreeBuilder> getSourceBuilds() {
        return sourceBuilds;
    }

    @Override
    public void exportProject(Project project) {
        exportedProjects.add(project);
    }
}
