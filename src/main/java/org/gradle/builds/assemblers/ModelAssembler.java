package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;

public interface ModelAssembler extends ProjectDecorator {
    /**
     * Populates the model for the given build. The projects, and their dependencies and components have already been configured.
     */
    void populate(Settings settings, Build build);
}
