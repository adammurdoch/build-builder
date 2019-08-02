package org.gradle.builds.model;

import org.gradle.builds.assemblers.GitRepoBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Allows a graph of builds to be defined.
 */
public class DefaultBuildTreeBuilder implements BuildTreeBuilder {
    private final Path rootDir;
    private final BuildSettingsBuilder build;
    private final List<DefaultBuildSettingsBuilder> builds = new ArrayList<>();
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
    public BuildSettingsBuilder getMainBuild() {
        return build;
    }

    @Override
    public BuildSettingsBuilder addBuild(String rootDir) {
        return addBuild(this.rootDir.resolve(rootDir));
    }

    @Override
    public BuildSettingsBuilder addBuild(Path rootDir) {
        DefaultBuildSettingsBuilder build = new DefaultBuildSettingsBuilder(rootDir);
        builds.add(build);
        return build;
    }

    @Override
    public List<? extends BuildSettingsBuilder> getBuilds() {
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
    public BuildTree<BuildProjectTreeBuilder> toModel() {
        Function<BuildSettingsBuilder, BuildProjectTreeBuilder> mapper = new BuildConstructor();
        BuildProjectTreeBuilder mainBuild = mapper.apply(build);
        List<BuildProjectTreeBuilder> builds = this.builds.stream().map(mapper).collect(Collectors.toList());
        List<GitRepo> repos = this.repos.stream().map(r -> new DefaultGitRepo(r.getRootDir(), r.getVersion())).collect(Collectors.toList());
        return new DefaultBuildTree(mainBuild, builds, repos);
    }

    private static class BuildConstructor implements Function<BuildSettingsBuilder, BuildProjectTreeBuilder> {
        Map<BuildSettingsBuilder, BuildProjectTreeBuilder> results = new HashMap<>();

        @Override
        public BuildProjectTreeBuilder apply(BuildSettingsBuilder builder) {
            BuildProjectTreeBuilder build = results.get(builder);
            if (build == null) {
                build = ((DefaultBuildSettingsBuilder)builder).toModel(this);
                results.put(builder, build);
            }
            return build;
        }
    }
}
