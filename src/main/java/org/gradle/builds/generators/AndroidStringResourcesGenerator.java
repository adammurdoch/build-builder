package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;

public class AndroidStringResourcesGenerator extends ComponentSpecificProjectFileGenerator<AndroidComponent> {
    public AndroidStringResourcesGenerator() {
        super(AndroidComponent.class, "src/main/res/values/strings.xml");
    }

    @Override
    protected void generate(Project project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<resources>");
        for (String resourceName : component.getStringResources()) {
            printWriter.println("    <string name='" + resourceName + "'>some value</string>");
        }
        printWriter.println("</resources>");
        printWriter.println();
    }
}
