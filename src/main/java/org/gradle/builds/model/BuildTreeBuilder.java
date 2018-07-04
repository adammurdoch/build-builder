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
public class BuildTreeBuilder {
    private final Path rootDir;
    private final BuildSettingsBuilder build;
    private final List<BuildSettingsBuilder> builds = new ArrayList<>();
    private final List<GitRepoBuilder> repos = new ArrayList<>();
    private final GitRepoBuilder repo;

    public BuildTreeBuilder(Path rootDir) {
        this.rootDir = rootDir;
        this.build = addBuild(rootDir);
        repo = new GitRepoBuilder(rootDir);
        repos.add(repo);
    }

    /**
     * Returns the root directory of this tree.
     */
    public Path getRootDir() {
        return rootDir;
    }

    /**
     * Returns the main build for this model.
     */
    public BuildSettingsBuilder getMainBuild() {
        return build;
    }

    /**
     * Adds a build to this tree.
     */
    public BuildSettingsBuilder addBuild(String rootDir) {
        return addBuild(this.rootDir.resolve(rootDir));
    }

    /**
     * Adds a build to this tree.
     */
    public BuildSettingsBuilder addBuild(Path rootDir) {
        BuildSettingsBuilder build = new BuildSettingsBuilder(rootDir);
        builds.add(build);
        return build;
    }

    public List<BuildSettingsBuilder> getBuilds() {
        return builds;
    }

    /**
     * Returns the main repo for this model.
     */
    public GitRepoBuilder getRepo() {
        return repo;
    }

    public GitRepoBuilder addRepo(Path rootDir) {
        GitRepoBuilder repo = new GitRepoBuilder(rootDir);
        repos.add(repo);
        return repo;
    }

    /**
     * Constructs the build tree from this builder.
     */
    public BuildTree toModel() {
        Function<BuildSettingsBuilder, Build> mapper = new BuildConstructor();
        Build mainBuild = mapper.apply(build);
        List<Build> builds = this.builds.stream().map(mapper).collect(Collectors.toList());
        List<GitRepo> repos = this.repos.stream().map(r -> new GitRepo(r.getRootDir(), r.getVersion())).collect(Collectors.toList());
        return new BuildTree(mainBuild, builds, repos);
    }

    private static class BuildConstructor implements Function<BuildSettingsBuilder, Build> {
        Map<BuildSettingsBuilder, Build> results = new HashMap<>();

        @Override
        public Build apply(BuildSettingsBuilder builder) {
            Build build = results.get(builder);
            if (build == null) {
                build = builder.toModel(this);
                results.put(builder, build);
            }
            return build;
        }
    }
}
