package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptBlock {
    private final String name;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public ScriptBlock(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
