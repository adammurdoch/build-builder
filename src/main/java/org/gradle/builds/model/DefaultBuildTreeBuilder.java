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
    private final BuildStructureBuilder build;
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
    public BuildStructureBuilder addBuild(Path rootDir) {
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
    public BuildTree<BuildProjectStructureBuilder> toModel() {
        Function<BuildStructureBuilder, BuildProjectStructureBuilder> mapper = new BuildConstructor();
        BuildProjectStructureBuilder mainBuild = mapper.apply(build);
        List<BuildProjectStructureBuilder> builds = this.builds.stream().map(mapper).collect(Collectors.toList());
        List<GitRepo> repos = this.repos.stream().map(r -> new DefaultGitRepo(r.getRootDir(), r.getVersion())).collect(Collectors.toList());
        return new DefaultBuildTree(mainBuild, builds, repos);
    }

    private static class BuildConstructor implements Function<BuildStructureBuilder, BuildProjectStructureBuilder> {
        Map<BuildStructureBuilder, BuildProjectStructureBuilder> results = new HashMap<>();

        @Override
        public BuildProjectStructureBuilder apply(BuildStructureBuilder builder) {
            BuildProjectStructureBuilder build = results.get(builder);
            if (build == null) {
                build = ((DefaultBuildStructureBuilder)builder).toModel(this);
                results.put(builder, build);
            }
            return build;
        }
    }
}
