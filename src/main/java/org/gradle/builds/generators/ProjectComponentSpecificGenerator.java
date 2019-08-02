package org.gradle.builds.generators;

import org.gradle.builds.model.Component;
import org.gradle.builds.model.ConfiguredBuild;
import org.gradle.builds.model.ConfiguredProject;

import java.io.IOException;

public abstract class ProjectComponentSpecificGenerator<T extends Component> extends ProjectFileGenerator {
    private final Class<T> type;

    protected ProjectComponentSpecificGenerator(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void generate(ConfiguredBuild build, ConfiguredProject project, FileGenerator fileGenerator) throws IOException {
        T component = project.component(type);
        if (component != null) {
            generate(build, project, component, fileGenerator);
        }
    }

    protected abstract void generate(ConfiguredBuild build, ConfiguredProject project, T component, FileGenerator fileGenerator) throws IOException;
}
