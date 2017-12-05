package org.gradle.builds.generators;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public interface FileGenerator {
    void generate(Path file, Consumer<PrintWriter> writer) throws IOException;

    void generate(Path file, InputStream content) throws IOException;

    List<Path> getGeneratedFiles();
}
