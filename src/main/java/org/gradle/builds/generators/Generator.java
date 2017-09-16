package org.gradle.builds.generators;

import java.io.IOException;

public interface Generator<T> {
    void generate(T model) throws IOException;
}
