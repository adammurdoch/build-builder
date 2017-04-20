package org.gradle.builds.generators;

import org.gradle.builds.model.Build;

import java.io.IOException;

public interface BuildGenerator {
    void generate(Build build) throws IOException;
}
