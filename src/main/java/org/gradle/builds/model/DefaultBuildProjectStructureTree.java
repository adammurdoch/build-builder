package org.gradle.builds.model;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultBuildProjectStructureTree extends AbstractBuildTree<DefaultBuildProjectStructureBuilder> {

    public DefaultBuildProjectStructureTree(DefaultBuildProjectStructureBuilder build, List<DefaultBuildProjectStructureBuilder> builds, List<GitRepo> repos) {
        super(build, builds, repos);
    }

    public DefaultBuildProjectTree toModel() {
        return new DefaultBuildProjectTree(getMainBuild().toModel(), getBuilds().stream().map(b -> b.toModel()).collect(Collectors.toList()), getRepos());
    }
}
