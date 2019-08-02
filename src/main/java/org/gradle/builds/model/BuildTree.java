package org.gradle.builds.model;

import java.util.List;

public interface BuildTree {
    Build getBuild();

    List<Build> getBuilds();

    List<GitRepo> getRepos();
}
