package org.gradle.builds.generators;

import org.gradle.builds.model.HasSwiftSource;
import org.gradle.builds.model.Project;
import org.gradle.builds.model.SwiftClass;
import org.gradle.builds.model.SwiftSourceFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class SwiftSourceGenerator extends ProjectComponentSpecificGenerator<HasSwiftSource> {
    public SwiftSourceGenerator() {
        super(HasSwiftSource.class);
    }

    @Override
    protected void generate(Project project, HasSwiftSource component) throws IOException {
        for (SwiftSourceFile swiftSource : component.getSourceFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/swift/" + swiftSource.getName());
            Files.createDirectories(sourceFile.getParent());
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
                printWriter.println("// GENERATED SOURCE FILE");
                printWriter.println();
                if (swiftSource.hasMainFunction()) {
                    printWriter.println("import Foundation");
                }
                for (String module : swiftSource.getModules()) {
                    printWriter.println("import " + module);
                }
                for (SwiftClass swiftClass : swiftSource.getClasses()) {
                    printWriter.println();
                    printWriter.println("public class " + swiftClass.getName() + " {");
                    printWriter.println("    public init() { }");
                    printWriter.println("    public func doSomething() {");
                    for (SwiftClass dep : swiftClass.getReferencedClasses()) {
                        String varName = dep.getName().toLowerCase();
                        printWriter.println("        let " + varName + " = " + dep.getName() + "()");
                        printWriter.println("        " + varName + ".doSomething()");
                    }
                    printWriter.println("    }");
                    printWriter.println("}");
                }
                if (swiftSource.hasMainFunction()) {
                    printWriter.println();
                    for (SwiftClass swiftClass : swiftSource.getMainFunctionReferencedClasses()) {
                        String varName = swiftClass.getName().toLowerCase();
                        printWriter.println("let " + varName + " = " + swiftClass.getName() + "()");
                        printWriter.println(varName + ".doSomething()");
                    }
                    printWriter.println("print(\"it works\");");
                }
                printWriter.println();
            }
        }
    }
}
