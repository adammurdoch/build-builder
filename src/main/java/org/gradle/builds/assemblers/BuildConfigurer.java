package org.gradle.builds.assemblers;

public interface BuildConfigurer<T> {
    /**
     * Populates the model for the given build.
     */
    void populate(T build);
}
