package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.nio.file.Path;

public class SwiftSourceGenerator extends ProjectComponentSpecificGenerator<HasSwiftSource> {
    public SwiftSourceGenerator() {
        super(HasSwiftSource.class);
    }

    @Override
    protected void generate(Build build, Project project, HasSwiftSource component, FileGenerator fileGenerator) throws IOException {
        Path srcDir = component.isSwiftPm() ? build.getRootDir().resolve("Sources/" + project.getName()) : project.getProjectDir().resolve("src/main/swift/");
        for (SwiftSourceFile swiftSource : component.getSourceFiles()) {
            generateSourceFile(srcDir, swiftSource, fileGenerator);
        }

        Path testDir = component.isSwiftPm() ? build.getRootDir().resolve("Tests/" + project.getName() + "Tests") : project.getProjectDir().resolve("src/test/swift/");
        for (SwiftSourceFile swiftSource : component.getTestFiles()) {
            generateTestSourceFile(testDir, swiftSource, fileGenerator);
        }
    }

    private void generateSourceFile(Path srcDir, SwiftSourceFile swiftSource, FileGenerator fileGenerator) throws IOException {
        Path sourceFile = srcDir.resolve(swiftSource.getName());
        fileGenerator.generate(sourceFile, printWriter -> {
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
                for (Dependency<SwiftClass> dep : swiftClass.getReferencedClasses()) {
                    SwiftClass targetClass = dep.getTarget();
                    String varName = targetClass.getName().toLowerCase();
                    printWriter.println("        let " + varName + " = " + targetClass.getName() + "()");
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
        });
    }

    private void generateTestSourceFile(Path srcDir, SwiftSourceFile swiftSource, FileGenerator fileGenerator) throws IOException {
        Path sourceFile = srcDir.resolve(swiftSource.getName());
        fileGenerator.generate(sourceFile, printWriter -> {
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println("import XCTest");
            for (String module : swiftSource.getModules()) {
                printWriter.println("import " + module);
            }
            printWriter.println();
            for (SwiftClass swiftClass : swiftSource.getClasses()) {
                printWriter.println("class " + swiftClass.getName() + ": XCTestCase {");
                printWriter.println("    func testOk() {");
                XCUnitTest unitTest = swiftClass.role(XCUnitTest.class);
                if (unitTest != null) {
                    printWriter.println("        let " + unitTest.getClassUnderTest().getName().toLowerCase() + " = " + unitTest.getClassUnderTest().getName() + "()");
                }
                printWriter.println("        XCTAssertEqual(1, 1)");
                printWriter.println("    }");
                printWriter.println("}");
                printWriter.println();
            }
        });
    }
}
