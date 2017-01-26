package org.gradle.builds.assemblers;

import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

public interface ProjectDecorator {
    void apply(Class<? extends Component> component, Project project);
}
