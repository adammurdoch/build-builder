package org.gradle.builds.generators;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CollectingFileGenerator implements FileGenerator {
    private List<Path> generatedFiles = new ArrayList<>();

    public List<Path> getGeneratedFiles() {
        return generatedFiles;
    }

    @Override
    public void generate(Path file, InputStream content) throws IOException {
        generatedFiles.add(file);
        Files.copy(content, file);
    }

    @Override
    public void generate(Path file, Consumer<PrintWriter> writer) throws IOException {
        generatedFiles.add(file);
        Files.createDirectories(file.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(file))) {
            writer.accept(printWriter);
        }
    }
}
