package org.gradle.builds.assemblers;

import org.gradle.builds.model.*;

import java.nio.file.Path;

public class HttpRepoModelStructureAssembler implements BuildTreeAssembler {
    private final ProjectInitializer buildInitAction;
    private final int libraryCount;
    private final int versionCount;

    public HttpRepoModelStructureAssembler(ProjectInitializer buildInitAction, int libraryCount, int versionCount) {
        this.buildInitAction = buildInitAction;
        this.libraryCount = libraryCount;
        this.versionCount = versionCount;
    }

    @Override
    public void attachBuilds(Settings settings, BuildTreeBuilder model) {
        Path repoDir = model.getRootDir().resolve("http-repo");
        Path serverDir = model.getRootDir().resolve("repo-server");

        BuildSettingsBuilder serverBuild = new BuildSettingsBuilder(serverDir, "HTTP server build", "repo");

        HttpRepository httpRepository = new HttpRepository(repoDir, 5005);
        HttpServerImplementation httpServerImplementation = new HttpServerImplementation(httpRepository);

        serverBuild.setSettings(new Settings(1, 1));
        serverBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction){
            @Override
            public void initRootProject(Project project) {
                project.addComponent(httpServerImplementation);
            }
        });
        serverBuild.publishAs(new PublicationTarget(httpRepository));
        serverBuild.setTypeNamePrefix("Repo");
        model.addBuild(serverBuild);

        for(int i = 1; i <= versionCount; i++) {
            Path externalSourceDir = model.getRootDir().resolve("external/v" + i);
            BuildSettingsBuilder libraryBuild = new BuildSettingsBuilder(externalSourceDir, "external libraries build v" + i, "ext");
            if (libraryCount == 1) {
                libraryBuild.setSettings(new Settings(1, 1));
                libraryBuild.setProjectInitializer(new LibraryRootProjectInitializer("Ext", buildInitAction));
            } else {
                libraryBuild.setSettings(new Settings(libraryCount + 1, 1));
                libraryBuild.setProjectInitializer(new EmptyRootProjectInitializer(buildInitAction));
            }
            libraryBuild.publishAs(new PublicationTarget(httpRepository));
            libraryBuild.setTypeNamePrefix("Ext");
            libraryBuild.setVersion(i + ".0.0");
            model.addBuild(libraryBuild);

            if (i == versionCount) {
                model.getMainBuild().dependsOn(libraryBuild);
            }

            httpServerImplementation.addSourceBuild(externalSourceDir);
        }
    }
}
