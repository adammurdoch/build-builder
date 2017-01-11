package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProjectScriptBlock extends Scope {
    private final Set<ScriptBlock> repositories = new LinkedHashSet<ScriptBlock>();

    public Set<ScriptBlock> getRepositories() {
        return repositories;
    }

    public void jcenter() {
        repositories.add(new ScriptBlock("jcenter"));
    }
}
