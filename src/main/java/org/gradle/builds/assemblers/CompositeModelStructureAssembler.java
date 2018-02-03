package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildTreeBuilder;

import java.util.Arrays;
import java.util.List;

public class CompositeModelStructureAssembler implements BuildTreeAssembler {
    private final List<BuildTreeAssembler> assemblers;

    public CompositeModelStructureAssembler(BuildTreeAssembler... assemblers) {
        this.assemblers = Arrays.asList(assemblers);
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        for (BuildTreeAssembler assembler : assemblers) {
            assembler.attachBuilds(settings, model);
        }
    }
}
