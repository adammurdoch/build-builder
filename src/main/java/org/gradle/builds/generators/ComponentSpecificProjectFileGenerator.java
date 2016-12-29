package org.gradle.builds.generators;

import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ComponentSpecificProjectFileGenerator<T extends Component> extends ProjectFileGenerator {
    private final Class<T> type;
    private final String filePath;

    public ComponentSpecificProjectFileGenerator(Class<T> type, String filePath) {
        this.type = type;
        this.filePath = filePath;
    }

    @Override
    protected void generate(Project project) throws IOException {
        T component = project.component(type);
        if (component == null) {
            return;
        }

        Path file = project.getProjectDir().resolve(filePath);
        Files.createDirectories(file.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(file))) {
            generate(project, component, printWriter);
        }
    }

    protected abstract void generate(Project project, T component, PrintWriter printWriter);
}
