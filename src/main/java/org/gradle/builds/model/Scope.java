package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Scope {
    private final Map<String, ScriptBlock> blocks = new LinkedHashMap<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public Set<ScriptBlock> getBlocks() {
        return new LinkedHashSet<>(blocks.values());
    }

    public ScriptBlock block(String name) {
        return blocks.computeIfAbsent(name, k -> new ScriptBlock(name));
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Property value can either be a {@link CharSequence} or a {@link Number}.
     */
    public void property(String name, Object value) {
        properties.put(name, value);
    }
}
