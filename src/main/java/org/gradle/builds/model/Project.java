package org.gradle.builds.model;

import org.gradle.builds.assemblers.Graph;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class Project {
    public enum Role {
        Application, Library, Empty;
    }

    private final int id;
    private final Project parent;
    private final String name;
    private final Path projectDir;
    private final BuildScript buildScript = new BuildScript();
    private final Set<Project> dependencies = new LinkedHashSet<>();
    private final Set<Component> components = new LinkedHashSet<>();
    private Role role = Role.Empty;
    private Graph classGraph;

    public Project(int id, Project parent, String name, Path projectDir) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.projectDir = projectDir;
    }

    @Override
    public String toString() {
        return getPath();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        if (parent == null) {
            return ":";
        }
        String parentPath = parent.getPath();
        if (parentPath.equals(":")) {
            return ":" + name;
        }
        return parentPath + ":" + name;
    }

    public Path getProjectDir() {
        return projectDir;
    }

    public Project getParent() {
        return parent;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BuildScript getBuildScript() {
        return buildScript;
    }

    public Set<Project> getDependencies() {
        return dependencies;
    }

    public void dependsOn(Project project) {
        this.dependencies.add(project);
    }

    public void setClassGraph(Graph classGraph) {
        this.classGraph = classGraph;
    }

    public Graph getClassGraph() {
        return classGraph;
    }

    public <T extends Component> T component(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    public <T extends Component> T addComponent(T component) {
        components.add(component);
        return component;
    }
}
