package org.gradle.builds.assemblers;

import org.gradle.builds.generators.FileGenerator;
import org.gradle.builds.generators.Generator;
import org.gradle.builds.model.BuildTree;
import org.gradle.builds.model.ConfiguredBuild;

import java.io.IOException;

public class ModelGenerator implements Generator<BuildTree<ConfiguredBuild>> {
    private final Generator<ConfiguredBuild> buildGenerator;

    public ModelGenerator(Generator<ConfiguredBuild> buildGenerator) {
        this.buildGenerator = buildGenerator;
    }

    @Override
    public void generate(BuildTree<ConfiguredBuild> model, FileGenerator fileGenerator) throws IOException {
        for (ConfiguredBuild build : model.getBuilds()) {
            System.out.println("* Generating " + build.getName());
            buildGenerator.generate(build, fileGenerator);
        }
    }
}
