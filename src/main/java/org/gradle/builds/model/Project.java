package org.gradle.builds.model;

import org.gradle.builds.assemblers.Graph;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface Project {
    String getName();

    String getPath();

    /**
     * Returns lower case '.' separated namespace for this project.
     */
    String getQualifiedNamespaceFor();

    /**
     * Returns a capital-cased identifier that can be used as a type name for this project.
     */
    String getTypeNameFor();

    void setTypeName(String typeName);

    /**
     * Returns a base name that can be used as a file name for this project.
     */
    String getFileNameFor();

    void setVersion(String version);

    String getVersion();

    Path getProjectDir();

    Project getParent();

    BuildScript getBuildScript();

    List<Dependency<Project>> getRequiredProjects();

    void requiresProject(Dependency<Project> project);

    void setClassGraph(Graph classGraph);

    Graph getClassGraph();

    <T extends Component> T component(Class<T> type);

    <T extends Component> T addComponent(T component);

    PublicationTarget getPublicationTarget();

    void publishAs(PublicationTarget publicationTarget);

    /**
     * Returns the libraries published by this project, if any.
     */
    List<PublishedLibrary<?>> getPublishedLibraries();

    /**
     * Returns the local libraries provided by this project, if any.
     */
    <T> List<LocalLibrary<? extends T>> getExportedLibraries(Class<T> type);

    void export(LocalLibrary<?> library);

    /**
     * Returns the libraries required by this project, if any.
     */
    <T> List<Dependency<Library<? extends T>>> requiredLibraries(Class<T> type);

    void requires(Dependency<Library<?>> dependency);

    /**
     * Adds an implementation dependency on the given library.
     */
    void requires(Library<?> library);

    /**
     * Adds implementation dependencies on the given libraries.
     */
    void requires(Collection<? extends Library<?>> libraries);
}
