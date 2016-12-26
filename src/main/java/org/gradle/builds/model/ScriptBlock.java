package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptBlock {
    private final String name;
    private final Map<String, String> properties = new LinkedHashMap<>();

    public ScriptBlock(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void property(String name, String value) {
        properties.put(name, value);
    }
}
