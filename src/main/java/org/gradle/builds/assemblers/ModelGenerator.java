package org.gradle.builds.assemblers;

import org.gradle.builds.generators.FileGenerator;
import org.gradle.builds.generators.Generator;
import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.BuildTree;

import java.io.IOException;

public class ModelGenerator implements Generator<BuildTree<BuildProjectStructureBuilder>> {
    private final Generator<BuildProjectStructureBuilder> buildGenerator;

    public ModelGenerator(Generator<BuildProjectStructureBuilder> buildGenerator) {
        this.buildGenerator = buildGenerator;
    }

    @Override
    public void generate(BuildTree<BuildProjectStructureBuilder> model, FileGenerator fileGenerator) throws IOException {
        for (BuildProjectStructureBuilder build : model.getBuilds()) {
            System.out.println("* Generating " + build);
            buildGenerator.generate(build, fileGenerator);
        }
    }
}
