package org.gradle.builds.generators;

import org.gradle.builds.model.BuildProjectStructureBuilder;
import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public abstract class ProjectComponentSpecificSingleFileGenerator<T extends Component> extends ProjectComponentSpecificGenerator<T> {
    private final String filePath;

    public ProjectComponentSpecificSingleFileGenerator(Class<T> type, String filePath) {
        super(type);
        this.filePath = filePath;
    }

    @Override
    protected void generate(BuildProjectStructureBuilder build, Project project, T component, FileGenerator fileGenerator) throws IOException {
        Path file = project.getProjectDir().resolve(filePath);
        fileGenerator.generate(file, printWriter -> {
            generate(project, component, printWriter);
        });
    }

    protected abstract void generate(Project project, T component, PrintWriter printWriter);
}
