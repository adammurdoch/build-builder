package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectComponentSpecificGenerator<T extends Component> extends ProjectFileGenerator {
    private final Class<T> type;

    protected ProjectComponentSpecificGenerator(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void generate(BuildProjectStructureBuilder build, Project project, FileGenerator fileGenerator) throws IOException {
        T component = project.component(type);
        if (component != null) {
            generate(build, project, component, fileGenerator);
        }
    }

    protected abstract void generate(BuildProjectStructureBuilder build, Project project, T component, FileGenerator fileGenerator) throws IOException;
}
