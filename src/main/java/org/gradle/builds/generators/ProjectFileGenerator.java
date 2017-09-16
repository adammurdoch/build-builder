package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectFileGenerator implements Generator<Build> {
    public void generate(Build build) throws IOException {
        for (Project project : build.getProjects()) {
            generate(build, project);
        }
    }

    protected abstract void generate(Build build, Project project) throws IOException;
}
