package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectTreeBuilder;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectFileGenerator implements Generator<BuildProjectTreeBuilder> {
    @Override
    public void generate(BuildProjectTreeBuilder build, FileGenerator fileGenerator) throws IOException {
        for (Project project : build.getProjects()) {
            generate(build, project, fileGenerator);
        }
    }

    protected abstract void generate(BuildProjectTreeBuilder build, Project project, FileGenerator fileGenerator) throws IOException;
}
