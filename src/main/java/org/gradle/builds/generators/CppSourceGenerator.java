package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class CppSourceGenerator extends ProjectComponentSpecificGenerator<HasCppSource> {
    public CppSourceGenerator() {
        super(HasCppSource.class);
    }

    @Override
    protected void generate(Build build, Project project, HasCppSource component, FileGenerator fileGenerator) throws IOException {
        MacroIncludes macroIncludes = component.getMacroIncludes();
        for (CppSourceFile cppSource : component.getSourceFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/cpp/" + cppSource.getName());
            writeSourceFile(cppSource, sourceFile, fileGenerator);
        }
        for (CppHeaderFile cppHeader : component.getPublicHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/public/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, true, macroIncludes, fileGenerator);
        }
        for (CppHeaderFile cppHeader : component.getImplementationHeaderFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/headers/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, false, macroIncludes, fileGenerator);
        }
        for (CppHeaderFile cppHeader : component.getPrivateHeadersFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/cpp/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, false, macroIncludes, fileGenerator);
        }
        for (CppHeaderFile cppHeader : component.getTestHeaders()) {
            Path sourceFile = project.getProjectDir().resolve("src/test/headers/" + cppHeader.getName());
            writeHeader(cppHeader, sourceFile, false, macroIncludes, fileGenerator);
        }
        for (CppSourceFile cppSource : component.getTestFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/test/cpp/" + cppSource.getName());
            writeSourceFile(cppSource, sourceFile, fileGenerator);
        }
    }

    private void writeSourceFile(CppSourceFile cppSource, Path sourceFile, FileGenerator fileGenerator) throws IOException {
        fileGenerator.generate(sourceFile, printWriter -> {
            String typeName = cppSource.getName().replace(".cpp", "");
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println();
            for (String headerFile : cppSource.getSystemHeaders()) {
                printWriter.println("#include <" + headerFile + ">");
            }
            for (CppHeaderFile headerFile : cppSource.getHeaderFiles()) {
                printWriter.println("#include \"" + headerFile.getName() + "\"");
            }
            printWriter.println("#include <iostream>");
            // Add some filler
            printWriter.println("#include <stdio.h>");

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
                printWriter.println("    return 0;");
                printWriter.println("}");
            }
            for (CppClass cppClass : cppSource.getClasses()) {
                printWriter.println();
                printWriter.println("int " + cppClass.getName() + "::visited = 0;");
                printWriter.println();
                functionHeader(printWriter);
                printWriter.println("void " + cppClass.getName() + "::doSomething() {");
                printWriter.println("    if (visited == 0) {");
                printWriter.println("        std::cout << \"visit " + cppClass.getName() + "\" << std::endl;");
                for (Dependency<CppClass> dep : cppClass.getReferencedClasses()) {
                    CppClass targetClass = dep.getTarget();
                    String varName = targetClass.getName().toLowerCase();
                    printWriter.println("        " + targetClass.getName() + " " + varName + ";");
                    printWriter.println("        " + varName + ".doSomething();");
                }
                printWriter.println("        visited = 1;");
                printWriter.println("    }");
                printWriter.println("}");
            }
            printWriter.println();
        });
    }

    private void writeHeader(CppHeaderFile cppHeader, Path sourceFile, boolean exported, MacroIncludes macroIncludes, FileGenerator fileGenerator)
            throws IOException {
        fileGenerator.generate(sourceFile, printWriter -> {
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
            printWriter.println();
            for (String headerFile : cppHeader.getSystemHeaders()) {
                printWriter.println("#include <" + headerFile + ">");
            }
            for (int i = 0; i < cppHeader.getHeaderFiles().size(); i++) {
                CppHeaderFile headerFile = cppHeader.getHeaderFiles().get(i);
                if (i == 0) {
                    switch (macroIncludes) {
                        case simple:
                            // Use a macro include for one of the includes
                            String macro = headerFile.getName().replace(".h", "_H").toUpperCase();
                            printWriter.println("#define " + macro + " \"" + headerFile.getName() + "\"");
                            printWriter.println("#include " + macro + "");
                            break;
                        case complex:
                            // Use a macro include for one of the includes
                            String macro1 = headerFile.getName().replace(".h", "_H").toUpperCase();
                            String macro2 = "__" + macro1;
                            printWriter.println("#define " + macro2 + "(X) #X");
                            printWriter.println("#define " + macro1 + " " + macro2 + "(" + headerFile.getName() + ")");
                            printWriter.println("#include " + macro1 + "");
                            break;
                    }
                }
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
                printWriter.println("  private:");
                printWriter.println("    static int visited;");
                printWriter.println("};");
            }
            printWriter.println();
            printWriter.println("#endif");
            printWriter.println();
        });
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
