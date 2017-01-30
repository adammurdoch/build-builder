package org.gradle.builds.assemblers;

import org.gradle.builds.model.Component;
import org.gradle.builds.model.Project;

public interface ProjectDecorator {
    /**
     * Applies the given type of component to a project, with relevant defaults.
     */
    void apply(Class<? extends Component> component, Project project);
}
