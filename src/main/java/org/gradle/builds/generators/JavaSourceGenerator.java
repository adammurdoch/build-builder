package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaSourceGenerator extends ProjectFileGenerator {
    private int counter = 1000;

    @Override
    protected void generate(Build build, Project project) throws IOException {
        HasJavaSource<?> component = project.component(HasJavaSource.class);
        if (component != null) {
            generate(project, component);
        }
    }

    private void generate(Project project, HasJavaSource<?> component) throws IOException {
        for (JavaClass javaClass : component.getSourceFiles()) {
            generateMainClass(project, javaClass);
        }

        for (JavaClass javaClass : component.getTestFiles()) {
            if (javaClass.role(UnitTest.class) != null) {
                generateUnitTest(project, javaClass, javaClass.role(UnitTest.class));
            } else if (javaClass.role(InstrumentedTest.class) != null) {
                generateInstrumentedTest(project, javaClass);
            }
        }
    }

    private void generateMainClass(Project project, JavaClass javaClass) throws IOException {
        Path sourceFile = project.getProjectDir().resolve("src/main/java/" + javaClass.getName().replace(".", "/") + ".java");
        Files.createDirectories(sourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println("package " + javaClass.getPackage() + ";");
            printWriter.println();
            if (javaClass.role(AndroidActivity.class) != null) {
                printWriter.println("public class " + javaClass.getSimpleName() + " extends android.app.Activity {");
            } else {
                printWriter.println("public class " + javaClass.getSimpleName() + " {");
            }
            printWriter.println("    // constant with unique value, referenced by other classes");
            printWriter.println("    public static final int INT_CONST = " + (counter++) + ";");
            printWriter.println("    // constant with unique value, not referenced by any other classes");
            printWriter.println("    public static final int UNUSED_INT_CONST = " + (counter++) + ";");
            printWriter.println("    // constant with unique value, not referenced by any other classes");
            printWriter.println("    public static final String STRING_CONST = \"" + javaClass.getName() + "\";");
            printWriter.println("    public static final Object NON_INLINED_CONST = new Object();");
            printWriter.println();
            printWriter.println("    private static final int PRIVATE_INT_CONST = 123;");
            printWriter.println("    private static final String PRIVATE_STRING_CONST = \"some value\";");
            printWriter.println();
            printWriter.println("    public String string1 = STRING_CONST;");
            printWriter.println("    private boolean field1 = true;");
            printWriter.println();
            printWriter.println("    private int calculateSomeValue() {");
            printWriter.println("        int total = 0;");
            printWriter.println("        for (int i = 0; i < INT_CONST; i++) {");
            printWriter.println("            total += i * 400;");
            printWriter.println("        }");
            printWriter.println("        return total;");
            printWriter.println("    }");
            printWriter.println();
            printWriter.println("    // public method referenced by other classes");
            printWriter.println("    public static String getSomeValue() {");
            for (String method : javaClass.getMethodReferences()) {
                printWriter.println("        " + method + ";");
            }
            for (String field : javaClass.getFieldReferences()) {
                printWriter.println("        String.valueOf(" + field + ");");
            }
            printWriter.println("        return STRING_CONST;");
            printWriter.println("    }");
            if (javaClass.role(AppEntryPoint.class) != null) {
                printWriter.println();
                printWriter.println("    public static void main(String[] args) {");
                printWriter.println("        System.out.println(\"greetings from \" + getSomeValue());");
                printWriter.println("    }");
            }
            if (javaClass.role(AndroidActivity.class) != null) {
                printWriter.println();
                printWriter.println("    public void onCreate(android.os.Bundle bundle) {");
                printWriter.println("        super.onCreate(bundle);");
                printWriter.println("        setContentView(R.layout." + project.getName().toLowerCase() + "_layout);");
                printWriter.println("        android.widget.TextView text = (android.widget.TextView)findViewById(R.id.textbox);");
                printWriter.println("        text.setText(getSomeValue());");
                printWriter.println("    }");
                AndroidComponent androidLibrary = project.component(AndroidComponent.class);
                for (JvmLibraryApi jvmLibraryApi : androidLibrary.getReferencedLibraries()) {
                    if (jvmLibraryApi instanceof AndroidLibraryApi) {
                        AndroidLibraryApi referencedLibrary = (AndroidLibraryApi) jvmLibraryApi;
                        printWriter.println();
                        printWriter.println("    public void click" + referencedLibrary.getIdentifier() + "(android.view.View view) {");
                        printWriter.println("        android.content.Intent intent = new android.content.Intent(this, " + referencedLibrary.getActivity().getName() + ".class);");
                        printWriter.println("        startActivity(intent);");
                        printWriter.println("    }");
                    }
                }
            }
            printWriter.println("}");
            printWriter.println();
        }
    }

    private void generateUnitTest(Project project, JavaClass javaClass, UnitTest unitTest) throws IOException {
        Path sourceFile = project.getProjectDir().resolve("src/test/java/" + javaClass.getName().replace(".", "/") + ".java");
        Files.createDirectories(sourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println("package " + javaClass.getPackage() + ";");
            printWriter.println();
            printWriter.println("public class " + javaClass.getSimpleName() + " {");
            printWriter.println("    @org.junit.Test");
            printWriter.println("    public void ok() {");
            printWriter.println("        " + unitTest.getClassUnderTest().getName() + ".getSomeValue();");
            printWriter.println("    }");
            printWriter.println("}");
            printWriter.println();
        }
    }

    private void generateInstrumentedTest(Project project, JavaClass javaClass) throws IOException {
        Path sourceFile = project.getProjectDir().resolve("src/androidTest/java/" + javaClass.getName().replace(".", "/") + ".java");
        Files.createDirectories(sourceFile.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(sourceFile))) {
            printWriter.println("// GENERATED SOURCE FILE");
            printWriter.println("package " + javaClass.getPackage() + ";");
            printWriter.println();
            printWriter.println("import android.support.test.runner.AndroidJUnit4;");
            printWriter.println("import org.junit.runner.RunWith;");
            printWriter.println("import org.junit.Test;");
            printWriter.println();
            printWriter.println("@RunWith(AndroidJUnit4.class)");
            printWriter.println("public class " + javaClass.getSimpleName() + " {");
            printWriter.println("    @Test");
            printWriter.println("    public void ok() {");
            printWriter.println("    }");
            printWriter.println("}");
            printWriter.println();
        }
    }
}
