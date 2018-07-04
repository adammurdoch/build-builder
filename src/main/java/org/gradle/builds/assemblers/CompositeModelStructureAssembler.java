package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildTreeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompositeModelStructureAssembler implements BuildTreeAssembler {
    private final List<BuildTreeAssembler> assemblers;

    public CompositeModelStructureAssembler(Collection<? extends BuildTreeAssembler> assemblers) {
        this.assemblers = new ArrayList<>(assemblers);
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        for (BuildTreeAssembler assembler : assemblers) {
            assembler.attachBuilds(settings, model);
        }
    }
}
