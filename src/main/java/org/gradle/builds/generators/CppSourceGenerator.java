package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class CppSourceGenerator extends ProjectComponentSpecificGenerator<HasCppSource> {
    public CppSourceGenerator() {
        super(HasCppSource.class);
    }

    @Override
    protected void generate(Build build, Project project, HasCppSource component) throws IOException {
        for (CppSourceFile cppSource : component.getSourceFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/cpp/" + cppSource.getName());
            writeSourceFile(cppSource, sourceFile);
        }
        for (CppHeaderFile cppHeader : component.getPublicHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/public/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, true);
        }
        for (CppHeaderFile cppHeader : component.getImplementationHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/headers/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, false);
        }
        for (CppHeaderFile cppHeader : component.getPrivateHeadersFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/cpp/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, false);
        }
    }

    private void writeSourceFile(CppSourceFile cppSource, Path sourceFile) throws IOException {
        Files.createDirectories(sourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
            String typeName = cppSource.getName().replace(".cpp", "");
            printWriter.println("// GENERATED SOURCE FILE");
            for (CppHeaderFile headerFile : cppSource.getHeaderFiles()) {
                printWriter.println("#include \"" + headerFile.getName() + "\"");
            }
            printWriter.println("#include <iostream>");

            // Add some filler
            for (int i = 0; i < 4; i++) {
                printWriter.println();
                functionHeader(printWriter);
                printWriter.println("int " + typeName + (i + 1) + "(int a, int b) {");
                printWriter.println("    return a + b;");
                printWriter.println("}");
            }

            if (cppSource.hasMainFunction()) {
                printWriter.println();
                functionHeader(printWriter);
                printWriter.println("int main() {");
                for (CppClass cppClass : cppSource.getMainFunctionReferencedClasses()) {
                    String varName = cppClass.getName().toLowerCase();
                    printWriter.println("    " + cppClass.getName() + " " + varName + ";");
                    printWriter.println("    " + varName + ".doSomething();");
                }
                printWriter.println("    std::cout << \"it works\" << std::endl;");
                printWriter.println("    return 0;");
                printWriter.println("}");
            }
            for (CppClass cppClass : cppSource.getClasses()) {
                printWriter.println();
                functionHeader(printWriter);
                printWriter.println("void " + cppClass.getName() + "::doSomething() {");
                for (Dependency<CppClass> dep : cppClass.getReferencedClasses()) {
                    CppClass targetClass = dep.getTarget();
                    String varName = targetClass.getName().toLowerCase();
                    printWriter.println("    " + targetClass.getName() + " " + varName + ";");
                    printWriter.println("    " + varName + ".doSomething();");
                }
                printWriter.println("}");
            }
            printWriter.println();
        }
    }

    private void writeHeader(CppHeaderFile cppHeader, Path sourceFile, boolean exported) throws IOException {
        Files.createDirectories(sourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
            String typeName = cppHeader.getName().replace(".h", "");
            String guardMacro = "__" + typeName.toUpperCase() + "__";
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println("#ifndef " + guardMacro);
            printWriter.println("#define " + guardMacro);
            if (exported) {
                printWriter.println();
                printWriter.println("#ifndef EXPORT_FUNC");
                printWriter.println("#ifdef _WIN32");
                printWriter.println("#define EXPORT_FUNC __declspec(dllexport)");
                printWriter.println("#else");
                printWriter.println("#define EXPORT_FUNC");
                printWriter.println("#endif");
                printWriter.println("#endif");
            }
            for (CppHeaderFile headerFile : cppHeader.getHeaderFiles()) {
                printWriter.println();
                printWriter.println("#include \"" + headerFile.getName() + "\"");
            }

            // Add some filler
            for (int i = 0; i < 4; i++) {
                printWriter.println();
                typeHeader(printWriter);
                printWriter.println("struct " + typeName + (i + 1) + " {");
                printWriter.println("    int x;");
                printWriter.println("    int y;");
                printWriter.println("};");
            }

            for (CppClass cppClass : cppHeader.getClasses()) {
                printWriter.println();
                typeHeader(printWriter);
                printWriter.println("class " + cppClass.getName() + " {");
                printWriter.println("  public:");
                if (exported) {
                    printWriter.println("    void EXPORT_FUNC doSomething();");
                } else {
                    printWriter.println("    void doSomething();");
                }
                for (Dependency<CppClass> dependency : cppClass.getReferencedClasses()) {
                    if (dependency.isApi()) {
                        printWriter.print("    void doSomethingWith(");
                        printWriter.print(dependency.getTarget().getName());
                        printWriter.println("& p);");
                    }
                }
                printWriter.println("};");
            }
            printWriter.println();
            printWriter.println("#endif");
            printWriter.println();
        }
    }

    private void typeHeader(PrintWriter printWriter) {
        printWriter.println("/*");
        printWriter.println(" * Here is a type declaration.");
        printWriter.println(" */");
    }

    private void functionHeader(PrintWriter printWriter) {
        printWriter.println("/*");
        printWriter.println(" * Here is a function.");
        printWriter.println(" */");
    }
}
