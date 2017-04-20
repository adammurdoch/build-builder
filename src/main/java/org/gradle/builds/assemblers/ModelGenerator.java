package org.gradle.builds.assemblers;

import org.gradle.builds.generators.BuildGenerator;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

import java.io.IOException;

public class ModelGenerator {
    private final BuildGenerator buildGenerator;

    public ModelGenerator(BuildGenerator buildGenerator) {
        this.buildGenerator = buildGenerator;
    }

    public void generate(Model model) throws IOException {
        for (Build build : model.getBuilds()) {
            buildGenerator.generate(build);
        }
    }
}
