package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

import java.util.Arrays;
import java.util.List;

public class CompositeProjectConfigurer implements ProjectConfigurer {
    private final List<? extends ProjectConfigurer> configurers;

    public CompositeProjectConfigurer(ProjectConfigurer... configurers) {
        this.configurers = Arrays.asList(configurers);
    }

    @Override
    public void configure(Settings settings, Project project) {
        for (ProjectConfigurer configurer : configurers) {
            configurer.configure(settings, project);
        }
    }
}
