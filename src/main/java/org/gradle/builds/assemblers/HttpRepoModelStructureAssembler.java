package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

public class HttpRepoModelStructureAssembler implements ModelStructureAssembler {
    private final ProjectInitializer buildInitAction;

    public HttpRepoModelStructureAssembler(ProjectInitializer buildInitAction) {
        this.buildInitAction = buildInitAction;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        Build repoBuild = new Build(model.getBuild().getRootDir().resolve("repo"), "repo");
        repoBuild.setSettings(new Settings(4, 1));
        repoBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
        HttpRepository httpRepository = new HttpRepository(repoBuild.getRootDir().resolve("build/repo"), 5005);
        repoBuild.publishAs(new PublicationTarget(httpRepository));
        repoBuild.getRootProject().addComponent(new HttpServerImplementation(httpRepository));
        repoBuild.setProjectNamePrefix("repo_");
        model.addBuild(repoBuild);

        model.getBuild().dependsOn(repoBuild);
    }
}
