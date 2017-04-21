package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.util.Arrays;
import java.util.List;

public class CompositeModelAssembler implements ModelAssembler {
    private final List<? extends ModelAssembler> assemblers;

    public CompositeModelAssembler(ModelAssembler... assemblers) {
        this.assemblers = Arrays.asList(assemblers);
    }

    @Override
    public void apply(Class<? extends Component> component, Project project) {
        for (ModelAssembler assembler : assemblers) {
            assembler.apply(component, project);
        }
    }

    @Override
    public void populate(Build build) {
        for (ModelAssembler assembler : assemblers) {
            assembler.populate(build);
        }
    }
}
