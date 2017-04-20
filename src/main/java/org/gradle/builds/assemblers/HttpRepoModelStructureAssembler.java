package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

public class HttpRepoModelStructureAssembler implements ModelStructureAssembler {
    @Override
    public void attachBuilds(Model model) {
        Build repoBuild = new Build(model.getBuild().getRootDir().resolve("repo"));
        model.setRepoBuild(repoBuild);
    }
}
