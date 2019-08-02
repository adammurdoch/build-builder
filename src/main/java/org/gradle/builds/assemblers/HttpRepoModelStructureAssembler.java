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
    public void populate(Settings settings, BuildTreeBuilder model) {
        BuildStructureBuilder serverBuild = model.addBuild("repo-server");
        serverBuild.setDisplayName("HTTP server build");
        serverBuild.setRootProjectName("repo");

        Path repoDir = model.getRootDir().resolve("http-repo");
        HttpRepository httpRepository = new HttpRepository(repoDir, 5005);
        HttpServerImplementation httpServerImplementation = new HttpServerImplementation(httpRepository);

        serverBuild.setSettings(new Settings(1, 1));
        serverBuild.getProjectInitializer().rootProject(project -> {
            project.addComponent(httpServerImplementation);
        });
        serverBuild.publishAs(new PublicationTarget(httpRepository));
        serverBuild.setTypeNamePrefix("Repo");

        for (int i = 1; i <= versionCount; i++) {
            Path externalSourceDir = model.getRootDir().resolve("external/v" + i);
            BuildStructureBuilder libraryBuild = model.addBuild(externalSourceDir);
            libraryBuild.setDisplayName("external libraries build v" + i);
            libraryBuild.setRootProjectName("ext");
            if (libraryCount == 1) {
                libraryBuild.setSettings(new Settings(1, 1));
                libraryBuild.getProjectInitializer().add(new LibraryRootProjectInitializer("Ext", buildInitAction));
            } else {
                libraryBuild.setSettings(new Settings(libraryCount + 1, 1));
                libraryBuild.getProjectInitializer().add(new EmptyRootProjectInitializer(buildInitAction));
            }
            libraryBuild.publishAs(new PublicationTarget(httpRepository));
            libraryBuild.setTypeNamePrefix("Ext");
            libraryBuild.setVersion(i + ".0.0");

            if (i == versionCount) {
                model.getMainBuild().dependsOn(libraryBuild);
            }

            httpServerImplementation.addSourceBuild(externalSourceDir);
        }
    }
}
