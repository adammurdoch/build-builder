package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.nio.file.Path;

public class HttpRepoModelStructureAssembler implements ModelStructureAssembler {
    private final ProjectInitializer buildInitAction;
    private final int libraryCount;
    private final int versionCount;

    public HttpRepoModelStructureAssembler(ProjectInitializer buildInitAction, int libraryCount, int versionCount) {
        this.buildInitAction = buildInitAction;
        this.libraryCount = libraryCount;
        this.versionCount = versionCount;
    }

    @Override
    public void attachBuilds(Settings settings, Model model) {
        Path repoDir = model.getBuild().getRootDir().resolve("http-repo");
        Path serverDir = model.getBuild().getRootDir().resolve("repo-server");

        Build serverBuild = new Build(serverDir, "repo");
        HttpRepository httpRepository = new HttpRepository(repoDir, 5005);

        serverBuild.setSettings(new Settings(1, 1));
        serverBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
        serverBuild.publishAs(new PublicationTarget(httpRepository));
        HttpServerImplementation httpServerImplementation = new HttpServerImplementation(httpRepository);
        serverBuild.getRootProject().addComponent(httpServerImplementation);
        serverBuild.setTypeNamePrefix("Repo");
        model.addBuild(serverBuild);

        for(int i = 0; i < versionCount; i++) {
            Path externalSourceDir = model.getBuild().getRootDir().resolve("external/v" + (i + 1));
            Build libraryBuild = new Build(externalSourceDir, "ext");
            libraryBuild.setSettings(new Settings(libraryCount + 1, 1));
            libraryBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
            libraryBuild.publishAs(new PublicationTarget(httpRepository));
            libraryBuild.setTypeNamePrefix("Ext");
            libraryBuild.setVersion((i + 1) + ".0");
            model.addBuild(libraryBuild);

            if (i == versionCount - 1) {
                model.getBuild().dependsOn(libraryBuild);
            }

            httpServerImplementation.addSourceBuild(externalSourceDir);
        }
    }
}
