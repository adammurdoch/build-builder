package org.gradle.builds.model;

import org.gradle.builds.assemblers.Graph;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultProject implements Project, ConfiguredProject {
    private final DefaultProject parent;
    private final String name;
    private final Path projectDir;
    private final BuildScript buildScript = new BuildScript();
    private final List<Dependency<Project>> requiredProjects = new ArrayList<>();
    private final Set<Component> components = new LinkedHashSet<>();
    private final List<LocalLibrary<?>> exportedLibraries = new ArrayList<>();
    private final List<Dependency<Library<?>>> requiredLibraries = new ArrayList<>();
    private String typeName;
    private Graph classGraph;
    private PublicationTarget publicationTarget;
    private String version;

    public DefaultProject(DefaultProject parent, String name, Path projectDir) {
        this.parent = parent;
        this.name = name;
        this.projectDir = projectDir;
    }

    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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
    @Override
    public String getQualifiedNamespaceFor() {
        if (parent == null) {
            return "org.gradle.example." + getTypeNameFor().toLowerCase();
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
    @Override
    public String getTypeNameFor() {
        if (typeName != null) {
            return typeName;
        }
        return mapName(name, (src, startOffset, endOffset, dest) -> {
            dest.append(Character.toUpperCase(src.charAt(startOffset)));
            dest.append(src.substring(startOffset + 1, endOffset));
        });
    }

    @Override
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Returns a base name that can be used as a file name for this project.
     */
    @Override
    public String getFileNameFor() {
        return typeName.toLowerCase();
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
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

    @Override
    public Path getProjectDir() {
        return projectDir;
    }

    @Override
    public DefaultProject getParent() {
        return parent;
    }

    @Override
    public BuildScript getBuildScript() {
        return buildScript;
    }

    @Override
    public List getRequiredProjects() {
        return requiredProjects;
    }

    @Override
    public void requiresProject(Dependency<Project> project) {
        this.requiredProjects.add(project);
    }

    @Override
    public void setClassGraph(Graph classGraph) {
        this.classGraph = classGraph;
    }

    @Override
    public Graph getClassGraph() {
        return classGraph;
    }

    @Override
    public <T extends Component> T component(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    @Override
    public <T extends Component> T addComponent(T component) {
        components.add(component);
        return component;
    }

    @Override
    public PublicationTarget getPublicationTarget() {
        return publicationTarget;
    }

    @Override
    public void publishAs(PublicationTarget publicationTarget) {
        this.publicationTarget = publicationTarget;
    }

    /**
     * Returns the libraries published by this project, if any.
     */
    @Override
    public List<PublishedLibrary<?>> getPublishedLibraries() {
        return exportedLibraries.stream().filter(d -> d.getPublished() != null).map(LocalLibrary::getPublished).collect(Collectors.toList());
    }

    /**
     * Returns the local libraries provided by this project, if any.
     */
    @Override
    public <T extends LibraryApi> List<LocalLibrary<? extends T>> getExportedLibraries(Class<T> type) {
        return exportedLibraries.stream().filter(d -> type.isInstance(d.getApi())).map(d -> (LocalLibrary<T>) d).collect(Collectors.toList());
    }

    @Override
    public void export(LocalLibrary<?> library) {
        exportedLibraries.add(library);
    }

    /**
     * Returns the libraries required by this project, if any.
     */
    @Override
    public <T> List requiredLibraries(Class<T> type) {
        return requiredLibraries.stream().filter(d -> type.isInstance(d.getTarget().getApi())).collect(Collectors.toList());
    }

    @Override
    public void requires(Dependency<Library<?>> dependency) {
        requiredLibraries.add(dependency);
    }

    /**
     * Adds an implementation dependency on the given library.
     */
    @Override
    public void requires(Library<?> library) {
        this.requiredLibraries.add(Dependency.implementation(library));
    }

    /**
     * Adds implementation dependencies on the given libraries.
     */
    @Override
    public void requires(Collection<? extends Library<?>> libraries) {
        for (Library<?> library : libraries) {
            requires(library);
        }
    }

    public ConfiguredProject toModel() {
        return this;
    }
}
