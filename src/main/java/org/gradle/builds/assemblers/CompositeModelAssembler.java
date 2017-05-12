package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;

import java.util.Arrays;
import java.util.List;

public class CompositeModelAssembler implements ModelAssembler {
    private final List<? extends ModelAssembler> assemblers;

    public CompositeModelAssembler(ModelAssembler... assemblers) {
        this.assemblers = Arrays.asList(assemblers);
    }

    @Override
    public void populate(Build build) {
        for (ModelAssembler assembler : assemblers) {
            assembler.populate(build);
        }
    }
}
