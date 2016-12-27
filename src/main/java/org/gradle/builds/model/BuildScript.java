package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BuildScript extends Scope {
    private final Set<String> buildScriptClasspath = new LinkedHashSet<>();
    private final Set<String> plugins = new LinkedHashSet<>();
    private final Set<SoftwareModelDeclaration> componentDeclarations = new LinkedHashSet<>();
    private final Map<String, Set<String>> dependencies = new LinkedHashMap<>();

    public Set<String> getPlugins() {
        return plugins;
    }

    public void requirePlugin(String id) {
        plugins.add(id);
    }

    public Set<String> getBuildScriptClasspath() {
        return buildScriptClasspath;
    }

    public void requireOnBuildScriptClasspath(String gav) {
        buildScriptClasspath.add(gav);
    }

    public Set<SoftwareModelDeclaration> getComponentDeclarations() {
        return componentDeclarations;
    }

    public SoftwareModelDeclaration componentDeclaration(String name, String type) {
        SoftwareModelDeclaration declaration = new SoftwareModelDeclaration(name, type);
        componentDeclarations.add(declaration);
        return declaration;
    }

    public Map<String, Set<String>> getDependencies() {
        return dependencies;
    }

    public void dependency(String scope, String projectPath) {
        Set<String> deps = dependencies.computeIfAbsent(scope, s -> new LinkedHashSet<>());
        deps.add(projectPath);
    }
}
