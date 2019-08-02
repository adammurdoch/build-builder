package org.gradle.builds.model;

import org.gradle.builds.assemblers.ComposableProjectInitializer;
import org.gradle.builds.assemblers.Settings;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
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
    private final Set<DefaultBuildStructureBuilder> dependsOn = new LinkedHashSet<>();
    private final Set<DefaultBuildStructureBuilder> includedBuilds = new LinkedHashSet<>();
    private final Set<DefaultBuildStructureBuilder> sourceBuilds = new LinkedHashSet<>();
    private DefaultBuildProjectStructureBuilder model;

    public DefaultBuildStructureBuilder(Path rootDir) {
        this.rootDir = rootDir;
    }

    public DefaultBuildProjectStructureBuilder toModel() {
        assertNotNull("displayName", displayName);
        assertNotNull("rootProjectName", rootProjectName);
        assertNotNull("settings", settings);
        assertNotNull("projectInitializer", projectInitializer);

        if (model == null) {
            Set<DefaultBuildProjectStructureBuilder> dependsOnBuilds = dependsOn.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<DefaultBuildProjectStructureBuilder> includedBuilds = this.includedBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<DefaultBuildProjectStructureBuilder> sourceBuilds = this.sourceBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            model = new DefaultBuildProjectStructureBuilder(rootDir, displayName, rootProjectName, settings, publicationTarget, typeNamePrefix, projectInitializer, version, dependsOnBuilds, includedBuilds, sourceBuilds);
        }
        return model;
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
        this.sourceBuilds.add((DefaultBuildStructureBuilder) childBuild);
    }

    @Override
    public Set<? extends BuildStructureBuilder> getSourceBuilds() {
        return sourceBuilds;
    }

    @Override
    public void dependsOn(BuildStructureBuilder childBuild) {
        this.dependsOn.add((DefaultBuildStructureBuilder) childBuild);
    }

    @Override
    public Set<? extends BuildStructureBuilder> getDependsOn() {
        return dependsOn;
    }

    @Override
    public void includeBuild(BuildStructureBuilder childBuild) {
        this.includedBuilds.add((DefaultBuildStructureBuilder) childBuild);
    }

    @Override
    public Set<? extends BuildStructureBuilder> getIncludedBuilds() {
        return includedBuilds;
    }
}
