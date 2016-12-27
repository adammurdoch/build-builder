package org.gradle.builds.generators;

import org.gradle.builds.model.*;

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
                for (CppHeaderFile headerFile : cppSource.getHeaderFiles()) {
                    printWriter.println("#include \"" + headerFile.getName() + "\"");
                }
                if (cppSource.hasMainFunction()) {
                    printWriter.println("#include <stdio.h>");
                    printWriter.println();
                    printWriter.println("int main() {");
                    for (CppClass cppClass : cppSource.getMainFunctionReferencedClasses()) {
                        String varName = cppClass.getName().toLowerCase();
                        printWriter.println("    " + cppClass.getName() + " " + varName + ";");
                        printWriter.println("    " + varName + ".doSomething();");
                    }
                    printWriter.println("    printf(\"it works\\n\");");
                    printWriter.println("    return 0;");
                    printWriter.println("}");
                }
                for (CppClass cppClass : cppSource.getClasses()) {
                    printWriter.println();
                    printWriter.println("void " + cppClass.getName() + "::doSomething() {");
                    for (CppClass dep : cppClass.getReferencedClasses()) {
                        String varName = dep.getName().toLowerCase();
                        printWriter.println("    " + dep.getName() + " " + varName + ";");
                        printWriter.println("    " + varName + ".doSomething();");
                    }
                    printWriter.println("}");
                }
                printWriter.println();
            }
        }

        for (CppHeaderFile cppHeader : component.getHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/headers/" + cppHeader.getName());
            Files.createDirectories(sourceFile.getParent());
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
                String macro = "_" + cppHeader.getName().replace(".", "_").toUpperCase() + "_";
                printWriter.println("// GENERATED SOURCE FILE");
                printWriter.println("#ifndef " + macro);
                printWriter.println("#define " + macro);
                for (CppHeaderFile headerFile : cppHeader.getHeaderFiles()) {
                    printWriter.println("#include \"" + headerFile.getName() + "\"");
                }
                for (CppClass cppClass : cppHeader.getClasses()) {
                    printWriter.println();
                    printWriter.println("class " + cppClass.getName() + " {");
                    printWriter.println("  public:");
                    printWriter.println("    void doSomething();");
                    printWriter.println("};");
                }
                printWriter.println();
                printWriter.println("#endif");
                printWriter.println();
            }
        }
    }
}
