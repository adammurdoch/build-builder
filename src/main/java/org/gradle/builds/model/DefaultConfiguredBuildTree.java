package org.gradle.builds.model;

import java.util.List;

public class DefaultConfiguredBuildTree extends AbstractBuildTree<ConfiguredBuild> {
    public DefaultConfiguredBuildTree(ConfiguredBuild build, List<ConfiguredBuild> builds, List<GitRepo> repos) {
        super(build, builds, repos);
    }
}
