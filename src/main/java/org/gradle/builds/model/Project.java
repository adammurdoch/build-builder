package org.gradle.builds.model;

import org.gradle.builds.assemblers.Graph;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Project {
    private final Project parent;
    private final String name;
    private final Path projectDir;
    private final BuildScript buildScript = new BuildScript();
    private final Set<Project> dependencies = new LinkedHashSet<>();
    private final Set<Component> components = new LinkedHashSet<>();
    private final List<LocalLibrary<?>> exportedLibraries = new ArrayList<>();
    private final List<PublishedLibrary<?>> externalDependencies = new ArrayList<>();
    private Graph classGraph;
    private PublicationTarget publicationTarget;

    public Project(Project parent, String name, Path projectDir) {
        this.parent = parent;
        this.name = name;
        this.projectDir = projectDir;
    }

    @Override
    public String toString() {
        return getPath();
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

    public PublicationTarget getPublicationTarget() {
        return publicationTarget;
    }

    public void publishAs(PublicationTarget publicationTarget) {
        this.publicationTarget = publicationTarget;
    }

    public List<PublishedLibrary<?>> getPublishedLibraries() {
        return exportedLibraries.stream().filter(d -> d.getPublished() != null).map(d -> d.getPublished()).collect(Collectors.toList());
    }

    public <T> List<LocalLibrary<? extends T>> getExportedLibraries(Class<T> type) {
        return exportedLibraries.stream().filter(d -> type.isInstance(d.getApi())).map(d -> (LocalLibrary<T>)d).collect(Collectors.toList());
    }

    public void export(LocalLibrary<?> library) {
        exportedLibraries.add(library);
    }

    public <T> List<PublishedLibrary<? extends T>> getExternalDependencies(Class<T> type) {
        return externalDependencies.stream().filter(d -> type.isInstance(d.getApi())).map(d -> (PublishedLibrary<T>)d).collect(Collectors.toList());
    }

    public void dependsOn(PublishedLibrary<?> library) {
        this.externalDependencies.add(library);
    }

    public void dependsOn(List<? extends PublishedLibrary<?>> libraries) {
        this.externalDependencies.addAll(libraries);
    }
}
