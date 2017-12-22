package org.gradle.builds.assemblers;

import org.gradle.builds.generators.FileGenerator;
import org.gradle.builds.generators.Generator;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

import java.io.IOException;

public class ModelGenerator implements Generator<Model> {
    private final Generator<Build> buildGenerator;

    public ModelGenerator(Generator<Build> buildGenerator) {
        this.buildGenerator = buildGenerator;
    }

    @Override
    public void generate(Model model, FileGenerator fileGenerator) throws IOException {
        for (Build build : model.getBuilds()) {
            System.out.println("* Generating " + build);
            buildGenerator.generate(build, fileGenerator);
        }
    }
}
