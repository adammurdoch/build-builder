package org.gradle.builds.model;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultBuildProjectTree extends AbstractBuildTree<DefaultBuildProjectBuilder> {

    public DefaultBuildProjectTree(DefaultBuildProjectBuilder build, List<DefaultBuildProjectBuilder> builds, List<GitRepo> repos) {
        super(build, builds, repos);
    }

    public DefaultConfiguredBuildTree toModel() {
        return new DefaultConfiguredBuildTree(getMainBuild().toModel(),
                getBuilds().stream().map(b -> b.toModel()).collect(Collectors.toList()),
                getRepos()
        );
    }
}
