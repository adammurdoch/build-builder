package org.gradle.builds.generators;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GitRepoGenerator implements Generator<Model> {
    @Override
    public void generate(Model model, FileGenerator fileGenerator) throws IOException {
        for (Build build : model.getBuilds()) {
            generate(model, build, fileGenerator);
        }
    }

    private void generate(Model model, Build build, FileGenerator fileGenerator) throws IOException {
        Path rootDir = build.getRootDir();
        List<Build> ignored = new ArrayList<>(model.getBuilds().size());

        fileGenerator.generate(rootDir.resolve(".gitignore"), writer -> {
            writer.println("build");
            writer.println(".gradle");
            for (Build other : model.getBuilds()) {
                if (other.getRootDir().startsWith(rootDir) && !other.getRootDir().equals(rootDir)) {
                    ignored.add(other);
                    writer.println(rootDir.relativize(other.getRootDir()));
                }
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
                    for (Build other : ignored) {
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
                CommitCommand commitCommand = git.commit();
                commitCommand.setMessage("Initial version");
                commitCommand.call();
            }
        } catch (GitAPIException e) {
            throw new RuntimeException("Could not create Git repository.", e);
        }
    }
}
