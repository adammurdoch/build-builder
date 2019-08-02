package org.gradle.builds.generators;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.builds.model.BuildTree;
import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.GitRepo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GitRepoGenerator implements Generator<BuildTree<ConfiguredBuild>> {
    @Override
    public void generate(BuildTree<ConfiguredBuild> model, FileGenerator fileGenerator) throws IOException {
        List<? extends GitRepo> repos = model.getRepos();
        for (GitRepo repo : repos) {
            generate(repo, repos, fileGenerator);
        }
    }

    private void generate(GitRepo repo, List<? extends GitRepo> allRepos, FileGenerator fileGenerator) throws IOException {
        Path rootDir = repo.getRootDir();
        List<GitRepo> children = new ArrayList<>();
        for (GitRepo other : allRepos) {
            if (other != repo && other.getRootDir().startsWith(rootDir)) {
                children.add(other);
            }
        }

        fileGenerator.generate(rootDir.resolve(".gitignore"), writer -> {
            writer.println("build");
            writer.println(".gradle");
            // Swift PM output
            writer.println(".build");
            // XCode
            writer.println("*.xcodeproj");
            writer.println("*.xcworkspace");
            // Visual studio
            writer.println(".vs");
            writer.println("*.sln");
            writer.println("*.sdf");
            writer.println("*.vcxproj");
            writer.println("*.vcxproj.filters");
            // IDEA
            writer.println("*.ipr");
            writer.println("*.iws");
            writer.println("*.iml");

            Set<String> dirs = new LinkedHashSet<>();
            for (GitRepo other : children) {
                Path relativePath = rootDir.relativize(other.getRootDir());
                dirs.add(relativePath.getName(0).toString());
            }
            for (String dir : dirs) {
                writer.println(dir);
            }
        });

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setWorkTree(rootDir.toFile());
        try {
            InitCommand init = Git.init();
            try (Git git = init.setDirectory(rootDir.toFile()).call()) {
                AddCommand addCommand = git.add();
                for (Path generatedFile : fileGenerator.getGeneratedFiles()) {
                    if (!generatedFile.startsWith(rootDir)) {
                        continue;
                    }
                    boolean skip = false;
                    for (GitRepo other : children) {
                        if (generatedFile.startsWith(other.getRootDir())) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    addCommand.addFilepattern(rootDir.relativize(generatedFile).toString());
                }
                addCommand.call();

                git.commit().setMessage("Initial version").call();
                git.tagDelete().setTags(repo.getVersion()).call();
                git.tag().setName(repo.getVersion()).call();
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(String.format("Could not create Git repository in %s.", rootDir), e);
        }
    }
}
