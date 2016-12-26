package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Scope {
    private final Map<String, ScriptBlock> blocks = new LinkedHashMap<>();

    public Set<ScriptBlock> getBlocks() {
        return new LinkedHashSet<>(blocks.values());
    }

    public ScriptBlock block(String name) {
        return blocks.computeIfAbsent(name, k -> new ScriptBlock(name));
    }
}
