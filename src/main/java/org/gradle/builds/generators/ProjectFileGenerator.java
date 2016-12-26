package org.gradle.builds.generators;

import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectFileGenerator {
    public void generate(Build build) throws IOException {
        for (Project project : build.getProjects()) {
            generate(project);
        }
    }

    protected abstract void generate(Project project) throws IOException;
}
