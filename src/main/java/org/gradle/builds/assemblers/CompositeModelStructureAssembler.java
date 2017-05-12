package org.gradle.builds.assemblers;

import org.gradle.builds.model.Model;

import java.util.Arrays;
import java.util.List;

public class CompositeModelStructureAssembler implements ModelStructureAssembler {
    private final List<ModelStructureAssembler> assemblers;

    public CompositeModelStructureAssembler(ModelStructureAssembler... assemblers) {
        this.assemblers = Arrays.asList(assemblers);
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        for (ModelStructureAssembler assembler : assemblers) {
            assembler.attachBuilds(settings, model);
        }
    }
}
