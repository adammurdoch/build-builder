package org.gradle.builds.generators;

import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.ConfiguredProject;

import java.io.IOException;

public abstract class ProjectFileGenerator implements Generator<ConfiguredBuild> {
    @Override
    public void generate(ConfiguredBuild build, FileGenerator fileGenerator) throws IOException {
        for (ConfiguredProject project : build.getProjects()) {
            generate(build, project, fileGenerator);
        }
    }

    protected abstract void generate(ConfiguredBuild build, ConfiguredProject project, FileGenerator fileGenerator) throws IOException;
}
