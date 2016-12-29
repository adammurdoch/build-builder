package org.gradle.builds.generators;

import org.gradle.builds.model.HasJavaSource;
import org.gradle.builds.model.JavaClass;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaSourceGenerator extends ProjectFileGenerator {
    @Override
    protected void generate(Project project) throws IOException {
        HasJavaSource component = project.component(HasJavaSource.class);
        if (component == null) {
            return;
        }

        for (JavaClass javaClass : component.getSourceFiles()) {
            Path sourceFile = project.getProjectDir().resolve("src/main/java/" + javaClass.getName().replace(".", "/") + ".java");
            Files.createDirectories(sourceFile.getParent());
            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
                printWriter.println("// GENERATED SOURCE FILE");
                printWriter.println("package " + javaClass.getPackage() + ";");
                printWriter.println();
                printWriter.println("public class " + javaClass.getSimpleName() + " {");
                printWriter.println("    public static String getSomeValue() {");
                for (JavaClass dep : javaClass.getReferencedClasses()) {
                    printWriter.println("        " + dep.getName() + ".getSomeValue();");
                }
                for (String field : javaClass.getFieldReferences()) {
                    printWriter.println("        String s = String.valueOf(" + field + ");");
                }
                printWriter.println("        return \"" + javaClass.getName() + "\";");
                printWriter.println("    }");
                if (javaClass.hasMainMethod()) {
                    printWriter.println();
                    printWriter.println("    public static void main(String[] args) {");
                    printWriter.println("        System.out.println(\"greetings from \" + getSomeValue());");
                    printWriter.println("    }");
                }
                printWriter.println("}");
                printWriter.println();
            }
        }
    }
}
