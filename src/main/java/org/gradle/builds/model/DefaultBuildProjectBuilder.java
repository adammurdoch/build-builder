package org.gradle.builds.model;

import org.gradle.builds.assemblers.Settings;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultBuildProjectBuilder implements BuildProjectBuilder {
    private final Path rootDir;
    private final Settings settings;
    private final DefaultProject rootProject;
    private final Set<DefaultProject> projects;
    private final Set<DefaultProject> subprojects;
    private final DefaultProject deepestProject;
    private final Set<DefaultBuildProjectBuilder> dependsOn;
    private final Set<DefaultBuildProjectBuilder> includedBuilds;
    private final Set<DefaultBuildProjectBuilder> sourceBuilds;

    private DefaultConfiguredBuild model;

    public DefaultBuildProjectBuilder(Path rootDir, Settings settings, DefaultProject rootProject, Set<DefaultProject> projects,
                                      Set<DefaultProject> subprojects, DefaultProject deepestProject,
                                      Set<DefaultBuildProjectBuilder> dependsOn, Set<DefaultBuildProjectBuilder> includedBuilds,
                                      Set<DefaultBuildProjectBuilder> sourceBuilds) {
        this.rootDir = rootDir;
        this.settings = settings;
        this.rootProject = rootProject;
        this.subprojects = subprojects;
        this.deepestProject = deepestProject;
        this.dependsOn = dependsOn;
        this.includedBuilds = includedBuilds;
        this.sourceBuilds = sourceBuilds;
        this.projects = projects;
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public Set<DefaultProject> getProjects() {
        return projects;
    }

    @Override
    public Set<? extends BuildProjectBuilder> getDependsOn() {
        return dependsOn;
    }

    @Override
    public Set<? extends BuildProjectBuilder> getIncludedBuilds() {
        return includedBuilds;
    }

    @Override
    public Set<? extends BuildProjectBuilder> getSourceBuilds() {
        return sourceBuilds;
    }

    public ConfiguredBuild toModel() {
        if (model == null) {
            Set<ConfiguredProject> projects = this.projects.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<ConfiguredProject> subprojects = this.subprojects.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<ConfiguredBuild> dependsOnBuilds = dependsOn.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<ConfiguredBuild> includedBuilds = this.includedBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            Set<ConfiguredBuild> sourceBuilds = this.sourceBuilds.stream().map(b -> b.toModel()).collect(Collectors.toSet());
            model = new DefaultConfiguredBuild(rootProject.getName(), rootDir, settings, rootProject.toModel(), projects,
                    subprojects, deepestProject.toModel(), dependsOnBuilds, includedBuilds, sourceBuilds);
        }
        return model;
    }
}
