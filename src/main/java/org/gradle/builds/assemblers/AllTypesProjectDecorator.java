package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

public class AllTypesProjectDecorator implements ProjectDecorator, ModelAssembler {
    private final JavaModelAssembler javaModelAssembler = new JavaModelAssembler();
    private final AndroidModelAssembler androidModelAssembler = new AndroidModelAssembler();
    private final CppModelAssembler cppModelAssembler = new CppModelAssembler();

    @Override
    public void populate(Settings settings, Build build) {
        javaModelAssembler.populate(settings, build);
        androidModelAssembler.populate(settings, build);
        cppModelAssembler.populate(settings, build);
    }

    @Override
    public void apply(Class<? extends Component> component, Project project) {
        javaModelAssembler.apply(component, project);
        androidModelAssembler.apply(component, project);
        cppModelAssembler.apply(component, project);
    }
}
