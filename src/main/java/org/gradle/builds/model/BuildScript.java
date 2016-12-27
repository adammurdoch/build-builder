package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BuildScript extends Scope {
    private final Set<String> buildScriptClasspath = new LinkedHashSet<>();
    private final Set<String> plugins = new LinkedHashSet<>();
    private final Map<String, String> componentDeclarations = new LinkedHashMap<>();

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

    public Map<String, String> getComponentDeclarations() {
        return componentDeclarations;
    }

    public void componentDeclaration(String name, String type) {
        componentDeclarations.put(name, type);
    }
}
