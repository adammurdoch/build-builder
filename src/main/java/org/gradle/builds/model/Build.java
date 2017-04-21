package org.gradle.builds.model;

import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Build {
    private final Path rootDir;
    private final Project rootProject;
    private final Map<String, Project> projects = new LinkedHashMap<>();
    private Settings settings;
    private boolean publish;
    private Class<? extends Component> rootProjectType = Application.class;

    public Build(Path rootDir, String rootProjectName) {
        this.rootDir = rootDir;
        rootProject = new Project(null, rootProjectName, rootDir);
        projects.put(rootProject.getPath(), rootProject);
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

    public Class<? extends Component> getRootProjectType() {
        return rootProjectType;
    }

    public void setRootProjectType(Class<? extends Component> rootProjectType) {
        this.rootProjectType = rootProjectType;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }
}
