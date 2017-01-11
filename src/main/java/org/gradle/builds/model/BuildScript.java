package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BuildScript extends ProjectScriptBlock {
    private final Set<ExternalDependencyDeclaration> buildScriptClasspath = new LinkedHashSet<>();
    private final Set<String> plugins = new LinkedHashSet<>();
    private final Set<SoftwareModelDeclaration> componentDeclarations = new LinkedHashSet<>();
    private final Map<String, Set<DependencyDeclaration>> dependencies = new LinkedHashMap<>();
    private ProjectScriptBlock allProjects;

    public ProjectScriptBlock getAllProjects() {
        return allProjects;
    }

    public ProjectScriptBlock allProjects() {
        if (allProjects == null) {
            allProjects = new ProjectScriptBlock();
        }
        return allProjects;
    }

    public Set<String> getPlugins() {
        return plugins;
    }

    public void requirePlugin(String id) {
        plugins.add(id);
    }

    public Set<ExternalDependencyDeclaration> getBuildScriptClasspath() {
        return buildScriptClasspath;
    }

    public void requireOnBuildScriptClasspath(String gav) {
        buildScriptClasspath.add(new ExternalDependencyDeclaration(gav));
    }

    public Set<SoftwareModelDeclaration> getComponentDeclarations() {
        return componentDeclarations;
    }

    public SoftwareModelDeclaration componentDeclaration(String name, String type) {
        SoftwareModelDeclaration declaration = new SoftwareModelDeclaration(name, type);
        componentDeclarations.add(declaration);
        return declaration;
    }

    public Map<String, Set<DependencyDeclaration>> getDependencies() {
        return dependencies;
    }

    private Set<DependencyDeclaration> getDepsForConfiguration(String configuration) {
        return dependencies.computeIfAbsent(configuration, s -> new LinkedHashSet<>());
    }

    public void dependsOnProject(String configuration, String projectPath) {
        Set<DependencyDeclaration> deps = getDepsForConfiguration(configuration);
        deps.add(new ProjectDependencyDeclaration(projectPath));
    }

    public void dependsOnExternal(String configuration, String gav) {
        Set<DependencyDeclaration> deps = getDepsForConfiguration(configuration);
        deps.add(new ExternalDependencyDeclaration(gav));
    }
}
