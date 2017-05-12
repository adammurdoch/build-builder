package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

public class AllTypesProjectDecorator implements ModelAssembler {
    private final JavaModelAssembler javaModelAssembler = new JavaModelAssembler();
    private final AndroidModelAssembler androidModelAssembler = new AndroidModelAssembler(false);
    private final CppModelAssembler cppModelAssembler = new CppModelAssembler();

    @Override
    public void populate(Build build) {
        javaModelAssembler.populate(build);
        androidModelAssembler.populate(build);
        cppModelAssembler.populate(build);
    }
}
