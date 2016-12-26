package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Project {
    private final Set<String> buildScriptClasspath = new LinkedHashSet<>();
    private final Set<String> plugins = new LinkedHashSet<>();
    private final Map<String, ScriptBlock> blocks = new LinkedHashMap<>();

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

    public Set<ScriptBlock> getBlocks() {
        return new LinkedHashSet<>(blocks.values());
    }

    public ScriptBlock block(String name) {
        ScriptBlock scriptBlock = blocks.get(name);
        if (scriptBlock == null) {
            scriptBlock = new ScriptBlock(name);
            blocks.put(name, scriptBlock);
        }
        return scriptBlock;
    }
}
