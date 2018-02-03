package org.gradle.builds.assemblers;

import org.gradle.builds.model.MutableBuildTree;

public interface BuildTreeAssembler {
    void attachBuilds(Settings settings, MutableBuildTree model);
}
