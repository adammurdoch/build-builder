package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlockWithDependencies extends Scope {
    private final Set<ScriptBlock> repositories = new LinkedHashSet<>();

    public Set<ScriptBlock> getRepositories() {
        return repositories;
    }

    public void jcenter() {
        repositories.add(new ScriptBlock("jcenter"));
    }

    public void google() {
        repositories.add(new ScriptBlock("google"));
    }

    public void mavenLocal() {
        repositories.add(new ScriptBlock("mavenCentral"));
    }

    public void maven(HttpRepository repo) {
        ScriptBlock block = new ScriptBlock("maven");
        block.property("url", repo.getUri().toString());
        repositories.add(block);
    }
}
