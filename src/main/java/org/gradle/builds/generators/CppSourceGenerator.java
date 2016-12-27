package org.gradle.builds.generators;

import org.gradle.builds.model.CppHeaderFile;
import org.gradle.builds.model.CppSourceFile;
import org.gradle.builds.model.HasNativeSource;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CppSourceGenerator extends ProjectFileGenerator {
    @Override
    protected void generate(Project project) throws IOException {
        HasNativeSource component = project.component(HasNativeSource.class);
        if (component == null) {
            return;
        }

        for (CppSourceFile cppSource : component.getSourceFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/cpp/" + cppSource.getName());
            Files.createDirectories(sourceFile.getParent());
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
                printWriter.println("// GENERATED SOURCE FILE");
                printWriter.println("#include <stdio.h>");
                printWriter.println("int main() {");
                printWriter.println("   printf(\"it works\\n\");");
                printWriter.println("   return 0;");
                printWriter.println("}");
            }
        }

        for (CppHeaderFile cppHeader : component.getHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/headers/" + cppHeader.getName());
            Files.createDirectories(sourceFile.getParent());
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
                printWriter.println("// GENERATED SOURCE FILE");
            }
        }
    }
}
