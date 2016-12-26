package org.gradle.builds.model;

public class ScriptBlock extends Scope {
    private final String name;

    public ScriptBlock(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
