package org.gradle.builds.assemblers;

import org.gradle.builds.model.Model;

public interface ModelStructureAssembler {
    void attachBuilds(Settings settings, Model model);
}
