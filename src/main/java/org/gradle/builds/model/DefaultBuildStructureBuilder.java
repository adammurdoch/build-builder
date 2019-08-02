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
public class DefaultBuildStructureBuilder implements BuildStructureBuilder {
    private final Path rootDir;
    private String displayName;
    private String rootProjectName;
    private Settings settings;
    private ComposableProjectInitializer projectInitializer = new ComposableProjectInitializer();
    private PublicationTarget publicationTarget;
    private String typeNamePrefix = "";
    private String version = "1.0.0";
    private final List<BuildStructureBuilder> dependsOn = new ArrayList<>();
    private final List<BuildStructureBuilder> includedBuilds = new ArrayList<>();
    private final List<BuildStructureBuilder> sourceBuilds = new ArrayList<>();

    public DefaultBuildStructureBuilder(Path rootDir) {
        this.rootDir = rootDir;
    }

    public BuildProjectStructureBuilder toModel(Function<BuildStructureBuilder, BuildProjectStructureBuilder> otherBuildLookup) {
        assertNotNull("displayName", displayName);
        assertNotNull("rootProjectName", rootProjectName);
        assertNotNull("settings", settings);
        assertNotNull("projectInitializer", projectInitializer);

        List<BuildProjectStructureBuilder> dependsOnBuilds = dependsOn.stream().map(otherBuildLookup).collect(Collectors.toList());
        List<BuildProjectStructureBuilder> includedBuilds = this.includedBuilds.stream().map(otherBuildLookup).collect(Collectors.toList());
        List<BuildProjectStructureBuilder> sourceBuilds = this.sourceBuilds.stream().map(otherBuildLookup).collect(Collectors.toList());
        return new DefaultBuildProjectStructureBuilder(rootDir, displayName, rootProjectName, settings, publicationTarget, typeNamePrefix, projectInitializer, version, dependsOnBuilds, includedBuilds, sourceBuilds);
    }

    private void assertNotNull(String name, @Nullable Object value) {
        if (value == null) {
            throw new IllegalStateException(String.format("No value specified for property '%s'", name));
        }
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setRootProjectName(String rootProjectName) {
        this.rootProjectName = rootProjectName;
    }

    @Override
    public String getRootProjectName() {
        return rootProjectName;
    }

    @Override
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public ComposableProjectInitializer getProjectInitializer() {
        return projectInitializer;
    }

    @Override
    public void setTypeNamePrefix(String typeNamePrefix) {
        this.typeNamePrefix = typeNamePrefix;
    }

    @Override
    public String getTypeNamePrefix() {
        return typeNamePrefix;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void publishAs(PublicationTarget publicationTarget) {
        this.publicationTarget = publicationTarget;
    }

    @Override
    public void sourceDependency(BuildStructureBuilder childBuild) {
        this.sourceBuilds.add(childBuild);
    }

    @Override
    public void dependsOn(BuildStructureBuilder childBuild) {
        this.dependsOn.add(childBuild);
    }

    @Override
    public void includeBuild(BuildStructureBuilder childBuild) {
        this.includedBuilds.add(childBuild);
    }
}
