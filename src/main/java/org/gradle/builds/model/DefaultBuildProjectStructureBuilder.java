package org.gradle.builds.model;

import org.gradle.builds.assemblers.ProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultBuildProjectStructureBuilder implements BuildProjectStructureBuilder {
    private final Path rootDir;
    private final String displayName;
    private final DefaultProject rootProject;
    private final Map<String, DefaultProject> projects = new LinkedHashMap<>();
    private final Set<DefaultBuildProjectStructureBuilder> dependsOn;
    private final Set<DefaultBuildProjectStructureBuilder> includedBuilds;
    private final Set<DefaultBuildProjectStructureBuilder> sourceBuilds;
    private final Set<Project> exportedProjects = new LinkedHashSet<>();
    private final Settings settings;
    private final PublicationTarget publicationTarget;
    private final String typeNamePrefix;
    private final ProjectInitializer projectInitializer;
    private final String version;
    private DefaultProject deepestProject;

    private DefaultBuildProjectBuilder model;

    public DefaultBuildProjectStructureBuilder(Path rootDir, String displayName, String rootProjectName, Settings settings, PublicationTarget publicationTarget, String typeNamePrefix, ProjectInitializer projectInitializer, String version, Set<DefaultBuildProjectStructureBuilder> dependsOn, Set<DefaultBuildProjectStructureBuilder> includedBuilds, Set<DefaultBuildProjectStructureBuilder> sourceBuilds) {
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
        rootProject = new DefaultProject(null, rootProjectName, rootDir);
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
    public Set<DefaultProject> getProjects() {
        return new LinkedHashSet<>(this.projects.values());
    }

    @Override
    public Set<DefaultProject> getSubprojects() {
        return new LinkedHashSet<>(projects.values().stream().filter(project -> project != rootProject).collect(Collectors.toList()));
    }

    @Override
    public Project addProject(String name) {
        return addProject(name, rootProject, rootDir.resolve(name));
    }

    @Override
    public DefaultProject addProject(String path, Path projectDir) {
        if (!path.startsWith(":")) {
            throw new IllegalArgumentException("Invalid path specified: " + path);
        }
        int pos = path.lastIndexOf(':');
        String parentPath = pos == 0 ? ":" : path.substring(0, pos);
        String name = path.substring(pos + 1);
        return addProject(name, getOrCreate(parentPath), projectDir);
    }

    private DefaultProject getOrCreate(String path) {
        if (path.equals(":")) {
            return rootProject;
        }

        DefaultProject project = projects.get(path);
        if (project != null) {
            return project;
        }
        return addProject(path, rootDir.resolve(path.replace(':', '/').substring(1)));
    }

    private DefaultProject addProject(String name, DefaultProject parent, Path projectDir) {
        DefaultProject project = new DefaultProject(parent, name, projectDir);
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
    public Set<? extends BuildProjectStructureBuilder> getDependsOn() {
        return dependsOn;
    }

    @Override
    public String getTypeNamePrefix() {
        return typeNamePrefix;
    }

    @Override
    public Set<? extends BuildProjectStructureBuilder> getIncludedBuilds() {
        return includedBuilds;
    }

    @Override
    public String getName() {
        return rootProject.getName();
    }

    @Override
    public void setDeepestProject(Project deepestProject) {
        this.deepestProject = (DefaultProject) deepestProject;
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
    public Set<? extends BuildProjectStructureBuilder> getSourceBuilds() {
        return sourceBuilds;
    }

    @Override
    public void exportProject(Project project) {
        exportedProjects.add(project);
    }

    public DefaultBuildProjectBuilder toModel() {
        if (model == null) {
            model = new DefaultBuildProjectBuilder(rootDir, settings,
                    rootProject,
                    getProjects(),
                    getSubprojects(),
                    deepestProject,
                    dependsOn.stream().map(b -> b.toModel()).collect(Collectors.toSet()),
                    includedBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet()),
                    sourceBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet())
            );
        }
        return model;
    }
}
