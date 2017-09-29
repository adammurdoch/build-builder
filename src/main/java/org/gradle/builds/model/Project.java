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
    private final List<Library<?>> requiredLibraries = new ArrayList<>();
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

    /**
     * Returns lower case '.' separated namespace for this project.
     */
    public String getQualifiedNamespaceFor() {
        if (parent == null) {
            return "org.gradle.example.app";
        }
        return "org.gradle.example" + mapName(name, (src, startOffset, endOffset, dest) -> {
            String element = src.substring(startOffset, endOffset).toLowerCase();
            if (Character.isJavaIdentifierStart(element.charAt(0))) {
                dest.append(".");
            }
            dest.append(element);
        });
    }

    /**
     * Returns a capital-cased identifier that can be used as a type name for this project.
     */
    public String getTypeNameFor() {
        if (parent == null) {
            return "App";
        }
        return mapName(name, (src, startOffset, endOffset, dest) -> {
            dest.append(Character.toUpperCase(src.charAt(startOffset)));
            dest.append(src.substring(startOffset + 1, endOffset));
        });
    }

    /**
     * Returns a base name that can be used as a file name for this project.
     */
    public String getFileNameFor() {
        if (parent == null) {
            return "app";
        }
        return name.toLowerCase();
    }

    private interface NameCollector {
        void append(String src, int startOffset, int endOffset, StringBuilder dest);
    }

    private String mapName(String name, NameCollector collector) {
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        while (pos < name.length()) {
            int sep = name.indexOf('_', pos);
            if (sep < 0) {
                collector.append(name, pos, name.length(), builder);
                break;
            }
            collector.append(name, pos, sep, builder);
            pos = sep + 1;
        }
        return builder.toString();
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

    public void requires(Project project) {
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

    /**
     * Returns the libraries published by this project, if any.
     */
    public List<PublishedLibrary<?>> getPublishedLibraries() {
        return exportedLibraries.stream().filter(d -> d.getPublished() != null).map(LocalLibrary::getPublished).collect(Collectors.toList());
    }

    /**
     * Returns the local libraries provided by this project, if any.
     */
    public <T> List<LocalLibrary<? extends T>> getExportedLibraries(Class<T> type) {
        return exportedLibraries.stream().filter(d -> type.isInstance(d.getApi())).map(d -> (LocalLibrary<T>)d).collect(Collectors.toList());
    }

    public void export(LocalLibrary<?> library) {
        exportedLibraries.add(library);
    }

    /**
     * Returns the libraries required by this project, if any.
     */
    public <T> List<Library<? extends T>> getRequiredLibraries(Class<T> type) {
        return requiredLibraries.stream().filter(d -> type.isInstance(d.getApi())).map(d -> (Library<T>)d).collect(Collectors.toList());
    }

    public void requires(Library<?> library) {
        this.requiredLibraries.add(library);
    }

    public void requires(List<? extends Library<?>> libraries) {
        this.requiredLibraries.addAll(libraries);
    }
}
