package org.gradle.builds.assemblers;

import org.gradle.builds.generators.FileGenerator;
import org.gradle.builds.generators.Generator;
import org.gradle.builds.model.BuildProjectTreeBuilder;
import org.gradle.builds.model.BuildTree;

import java.io.IOException;

public class ModelGenerator implements Generator<BuildTree<BuildProjectTreeBuilder>> {
    private final Generator<BuildProjectTreeBuilder> buildGenerator;

    public ModelGenerator(Generator<BuildProjectTreeBuilder> buildGenerator) {
        this.buildGenerator = buildGenerator;
    }

    @Override
    public void generate(BuildTree<BuildProjectTreeBuilder> model, FileGenerator fileGenerator) throws IOException {
        for (BuildProjectTreeBuilder build : model.getBuilds()) {
            System.out.println("* Generating " + build);
            buildGenerator.generate(build, fileGenerator);
        }
    }
}
