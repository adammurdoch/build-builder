package org.gradle.builds.model;

import org.gradle.builds.assemblers.ComposableProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Allows the settings for a build to be configured.
 */
public class BuildSettingsBuilder {
    private final Path rootDir;
    private String displayName;
    private String rootProjectName;
    private Settings settings;
    private ComposableProjectInitializer projectInitializer = new ComposableProjectInitializer();
    private PublicationTarget publicationTarget;
    private String typeNamePrefix = "";
    private String version = "1.0.0";
    private final List<BuildSettingsBuilder> dependsOn = new ArrayList<>();
    private final List<BuildSettingsBuilder> includedBuilds = new ArrayList<>();
    private final List<BuildSettingsBuilder> sourceBuilds = new ArrayList<>();

    public BuildSettingsBuilder(Path rootDir) {
        this.rootDir = rootDir;
    }

    public Build toModel(Function<BuildSettingsBuilder, Build> otherBuildLookup) {
        assertNotNull("displayName", displayName);
        assertNotNull("rootProjectName", rootProjectName);
        assertNotNull("settings", settings);
        assertNotNull("projectInitializer", projectInitializer);

        List<Build> dependsOnBuilds = dependsOn.stream().map(otherBuildLookup).collect(Collectors.toList());
        List<Build> includedBuilds = this.includedBuilds.stream().map(otherBuildLookup).collect(Collectors.toList());
        List<Build> sourceBuilds = this.sourceBuilds.stream().map(otherBuildLookup).collect(Collectors.toList());
        return new Build(rootDir, displayName, rootProjectName, settings, publicationTarget, typeNamePrefix, projectInitializer, version, dependsOnBuilds, includedBuilds, sourceBuilds);
    }

    private void assertNotNull(String name, @Nullable Object value) {
        if (value == null) {
            throw new IllegalStateException(String.format("No value specified for property '%s'", name));
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setRootProjectName(String rootProjectName) {
        this.rootProjectName = rootProjectName;
    }

    public String getRootProjectName() {
        return rootProjectName;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public ComposableProjectInitializer getProjectInitializer() {
        return projectInitializer;
    }

    public void setTypeNamePrefix(String typeNamePrefix) {
        this.typeNamePrefix = typeNamePrefix;
    }

    public String getTypeNamePrefix() {
        return typeNamePrefix;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void publishAs(PublicationTarget publicationTarget) {
        this.publicationTarget = publicationTarget;
    }

    public void sourceDependency(BuildSettingsBuilder childBuild) {
        this.sourceBuilds.add(childBuild);
    }

    public void dependsOn(BuildSettingsBuilder childBuild) {
        this.dependsOn.add(childBuild);
    }

    public void includeBuild(BuildSettingsBuilder childBuild) {
        this.includedBuilds.add(childBuild);
    }
}
