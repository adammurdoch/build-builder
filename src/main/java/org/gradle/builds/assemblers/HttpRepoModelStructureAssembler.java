package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.nio.file.Path;

public class HttpRepoModelStructureAssembler implements ModelStructureAssembler {
    private final ProjectInitializer buildInitAction;
    private final int libraryCount;

    public HttpRepoModelStructureAssembler(ProjectInitializer buildInitAction, int libraryCount) {
        this.buildInitAction = buildInitAction;
        this.libraryCount = libraryCount;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        Path externalBuildDir = model.getBuild().getRootDir().resolve("external");
        Path repoDir = model.getBuild().getRootDir().resolve("http-repo");
        Path serverDir = model.getBuild().getRootDir().resolve("repo-server");

        Build serverBuild = new Build(serverDir, "repo");
        HttpRepository httpRepository = new HttpRepository(repoDir, 5005);

        serverBuild.setSettings(new Settings(1, 1));
        serverBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
        serverBuild.publishAs(new PublicationTarget(httpRepository));
        serverBuild.getRootProject().addComponent(new HttpServerImplementation(httpRepository, externalBuildDir));
        serverBuild.setTypeNamePrefix("Repo");
        model.addBuild(serverBuild);

        Build libraryBuild = new Build(externalBuildDir, "ext");
        libraryBuild.setSettings(new Settings(libraryCount + 1, 1));
        libraryBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
        libraryBuild.publishAs(new PublicationTarget(httpRepository));
        libraryBuild.setTypeNamePrefix("Ext");
        model.addBuild(libraryBuild);

        model.getBuild().dependsOn(libraryBuild);
    }
}
