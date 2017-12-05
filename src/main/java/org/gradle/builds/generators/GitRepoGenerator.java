package org.gradle.builds.generators;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.builds.model.Build;

import java.io.IOException;
import java.nio.file.Path;

public class GitRepoGenerator implements Generator<Build> {
    @Override
    public void generate(Build model, FileGenerator fileGenerator) throws IOException {
        Path rootDir = model.getRootDir();

        fileGenerator.generate(rootDir.resolve(".gitignore"), writer -> {
            writer.println("build");
            writer.println(".gradle");
        });

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setWorkTree(rootDir.toFile());
        try (Repository repository = builder.build()) {
            try {
                repository.create();
                Git git = new Git(repository);
                AddCommand addCommand = git.add();
                for (Path generatedFile : fileGenerator.getGeneratedFiles()) {
                    addCommand.addFilepattern(rootDir.relativize(generatedFile).toString());
                }
                addCommand.call();
                CommitCommand commitCommand = git.commit();
                commitCommand.setMessage("Initial version");
                commitCommand.call();
            } catch (GitAPIException e) {
                throw new RuntimeException("Could not create Git repository.", e);
            }
        }
    }
}
