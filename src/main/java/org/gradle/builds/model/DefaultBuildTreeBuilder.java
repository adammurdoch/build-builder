package org.gradle.builds.model;

import org.gradle.builds.assemblers.GitRepoBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows a graph of builds to be defined.
 */
public class DefaultBuildTreeBuilder implements BuildTreeBuilder {
    private final Path rootDir;
    private final DefaultBuildStructureBuilder build;
    private final List<DefaultBuildStructureBuilder> builds = new ArrayList<>();
    private final List<GitRepoBuilder> repos = new ArrayList<>();
    private final GitRepoBuilder repo;

    public DefaultBuildTreeBuilder(Path rootDir) {
        this.rootDir = rootDir;
        this.build = addBuild(rootDir);
        repo = new GitRepoBuilder(rootDir);
        repos.add(repo);
    }

    @Override
    public Path getRootDir() {
        return rootDir;
    }

    @Override
    public BuildStructureBuilder getMainBuild() {
        return build;
    }

    @Override
    public BuildStructureBuilder addBuild(String rootDir) {
        return addBuild(this.rootDir.resolve(rootDir));
    }

    @Override
    public DefaultBuildStructureBuilder addBuild(Path rootDir) {
        DefaultBuildStructureBuilder build = new DefaultBuildStructureBuilder(rootDir);
        builds.add(build);
        return build;
    }

    @Override
    public List<? extends BuildStructureBuilder> getBuilds() {
        return builds;
    }

    @Override
    public GitRepoBuilder getRepo() {
        return repo;
    }

    @Override
    public GitRepoBuilder addRepo(Path rootDir) {
        GitRepoBuilder repo = new GitRepoBuilder(rootDir);
        repos.add(repo);
        return repo;
    }

    @Override
    public List<? extends GitRepo> getRepos() {
        return repos;
    }

    /**
     * Constructs the build tree from this builder.
     */
    public DefaultBuildProjectStructureTree toModel() {
        DefaultBuildProjectStructureBuilder mainBuild = build.toModel();
        List<DefaultBuildProjectStructureBuilder> builds = this.builds.stream().map(b -> b.toModel()).collect(Collectors.toList());
        List<GitRepo> repos = this.repos.stream().map(r -> new DefaultGitRepo(r.getRootDir(), r.getVersion())).collect(Collectors.toList());
        return new DefaultBuildProjectStructureTree(mainBuild, builds, repos);
    }
}
