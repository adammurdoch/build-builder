package org.gradle.builds.model;

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

    public BuildTreeBuilder(Path rootDir, BuildSettingsBuilder build) {
        this.rootDir = rootDir;
        this.build = build;
        builds.add(build);
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
    public void addBuild(BuildSettingsBuilder build) {
        builds.add(build);
    }

    public List<BuildSettingsBuilder> getBuilds() {
        return builds;
    }

    /**
     * Constructs the build tree from this builder.
     */
    public Model toModel() {
        Function<BuildSettingsBuilder, Build> mapper = new BuildConstructor();
        return new Model(mapper.apply(build), builds.stream().map(mapper).collect(Collectors.toList()));
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
