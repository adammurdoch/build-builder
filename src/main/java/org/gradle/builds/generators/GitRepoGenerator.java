package org.gradle.builds.generators;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            Set<String> dirs = new LinkedHashSet<>();
            for (Build other : model.getBuilds()) {
                if (other.getRootDir().startsWith(rootDir) && !other.getRootDir().equals(rootDir)) {
                    ignored.add(other);
                    Path relativePath = rootDir.relativize(other.getRootDir());
                    dirs.add(relativePath.getName(0).toString());
                }
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

                git.commit().setMessage("Initial version").call();
                git.tagDelete().setTags(build.getVersion()).call();
                git.tag().setName(build.getVersion()).call();
            }
        } catch (GitAPIException e) {
            throw new RuntimeException("Could not create Git repository.", e);
        }
    }
}
