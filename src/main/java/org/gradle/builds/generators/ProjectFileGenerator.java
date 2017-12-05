package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectFileGenerator implements Generator<Build> {
    @Override
    public void generate(Build build, FileGenerator fileGenerator) throws IOException {
        for (Project project : build.getProjects()) {
            generate(build, project, fileGenerator);
        }
    }

    protected abstract void generate(Build build, Project project, FileGenerator fileGenerator) throws IOException;
}
