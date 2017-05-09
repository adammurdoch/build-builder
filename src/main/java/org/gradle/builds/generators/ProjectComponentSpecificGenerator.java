package org.gradle.builds.generators;

import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.io.IOException;

public abstract class ProjectComponentSpecificGenerator<T extends Component> extends ProjectFileGenerator {
    private final Class<T> type;

    public ProjectComponentSpecificGenerator(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void generate(Project project) throws IOException {
        T component = project.component(type);
        if (component != null) {
            generate(project, component);
        }
    }

    protected abstract void generate(Project project, T component) throws IOException;
}
