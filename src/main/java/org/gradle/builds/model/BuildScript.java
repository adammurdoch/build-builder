package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BuildScript extends ProjectScriptBlock {
    private final Set<ExternalDependencyDeclaration> buildScriptClasspath = new LinkedHashSet<>();
    private final Map<String, Set<DependencyDeclaration>> dependencies = new LinkedHashMap<>();
    private ProjectScriptBlock allProjects;
    private BlockWithDependencies buildscriptBlock;

    public ProjectScriptBlock getAllProjects() {
        return allProjects;
    }

    public ProjectScriptBlock allProjects() {
        if (allProjects == null) {
            allProjects = new ProjectScriptBlock();
        }
        return allProjects;
    }

    public BlockWithDependencies getBuildScriptBlock() {
        return buildscriptBlock;
    }

    public BlockWithDependencies buildScriptBlock() {
        if (buildscriptBlock == null) {
            buildscriptBlock = new BlockWithDependencies();
        }
        return buildscriptBlock;
    }

    public Set<ExternalDependencyDeclaration> getBuildScriptClasspath() {
        return buildScriptClasspath;
    }

    public void requireOnBuildScriptClasspath(String gav) {
        buildScriptClasspath.add(new ExternalDependencyDeclaration(gav));
    }

    public Map<String, Set<DependencyDeclaration>> getDependencies() {
        return dependencies;
    }

    private Set<DependencyDeclaration> getDepsForConfiguration(String configuration) {
        return dependencies.computeIfAbsent(configuration, s -> new LinkedHashSet<>());
    }

    public void dependsOnProject(String configuration, String projectPath) {
        dependsOn(configuration, new ProjectDependencyDeclaration(projectPath));
    }

    public void dependsOnExternal(String configuration, String gav) {
        dependsOn(configuration, new ExternalDependencyDeclaration(gav));
    }

    public void dependsOn(String configuration, DependencyDeclaration dep) {
        Set<DependencyDeclaration> deps = getDepsForConfiguration(configuration);
        deps.add(dep);
    }
}
