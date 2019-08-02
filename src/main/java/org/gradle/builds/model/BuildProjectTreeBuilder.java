package org.gradle.builds.model;

import org.gradle.builds.assemblers.ProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * A build whose relationships to other builds are read-only, and whose project structure is mutable.
 */
public interface BuildProjectTreeBuilder extends Build {
    Path getRootDir();

    Project getRootProject();

    Set<Project> getProjects();

    Set<Project> getSubprojects();

    Project addProject(String name);

    Project addProject(String path, Path projectDir);

    ProjectInitializer getProjectInitializer();

    Settings getSettings();

    List<PublishedLibrary<?>> getExportedLibraries();

    PublicationTarget getPublicationTarget();

    List<BuildProjectTreeBuilder> getDependsOn();

    String getTypeNamePrefix();

    List<BuildProjectTreeBuilder> getIncludedBuilds();

    String getName();

    void setDeepestProject(Project deepestProject);

    Project getDeepestProject();

    String getVersion();

    List<BuildProjectTreeBuilder> getSourceBuilds();

    void exportProject(Project project);
}
