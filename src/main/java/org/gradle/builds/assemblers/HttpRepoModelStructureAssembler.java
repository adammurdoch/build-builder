package org.gradle.builds.assemblers;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.HttpServer;
import org.gradle.builds.model.Model;

public class HttpRepoModelStructureAssembler implements ModelStructureAssembler {
    private final ModelStructureAssembler assembler;

    public HttpRepoModelStructureAssembler(ModelStructureAssembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        assembler.attachBuilds(settings, model);
        Build repoBuild = new Build(model.getBuild().getRootDir().resolve("repo"), "repo");
        repoBuild.setSettings(new Settings(4, 1));
        repoBuild.setRootProjectType(HttpServer.class);
        repoBuild.setPublish(true);
        model.setRepoBuild(repoBuild);
    }
}
