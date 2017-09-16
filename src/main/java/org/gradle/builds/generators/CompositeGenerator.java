package org.gradle.builds.generators;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CompositeGenerator<T> implements Generator<T> {
    private final List<? extends Generator<T>> generators;

    public CompositeGenerator(Generator<T>... generators) {
        this.generators = Arrays.asList(generators);
    }

    @Override
    public void generate(T build) throws IOException {
        for (Generator<T> generator : generators) {
            generator.generate(build);
        }
    }
}
