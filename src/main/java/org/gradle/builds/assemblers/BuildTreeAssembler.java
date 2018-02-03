package org.gradle.builds.assemblers;

import org.gradle.builds.model.BuildTreeBuilder;

public interface BuildTreeAssembler {
    void attachBuilds(Settings settings, BuildTreeBuilder model);
}
