package org.gradle.builds.generators;

import org.gradle.builds.model.Build;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CompositeBuildGenerator implements BuildGenerator {
    private final List<? extends BuildGenerator> generators;

    public CompositeBuildGenerator(BuildGenerator... generators) {
        this.generators = Arrays.asList(generators);
    }

    @Override
    public void generate(Build build) throws IOException {
        for (BuildGenerator generator : generators) {
            generator.generate(build);
        }
    }
}
