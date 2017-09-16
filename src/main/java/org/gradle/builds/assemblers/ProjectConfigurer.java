package org.gradle.builds.assemblers;

import org.gradle.builds.model.Project;

public interface ProjectConfigurer {
    /**
     * Called after dependencies have been configured.
     */
    void configure(Settings settings, Project project);
}
