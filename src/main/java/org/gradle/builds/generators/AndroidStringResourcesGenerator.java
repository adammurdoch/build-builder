package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.ConfiguredProject;

import java.io.PrintWriter;
import java.util.Map;

public class AndroidStringResourcesGenerator extends ProjectComponentSpecificSingleFileGenerator<AndroidComponent> {
    public AndroidStringResourcesGenerator() {
        super(AndroidComponent.class, "src/main/res/values/strings.xml");
    }

    @Override
    protected void generate(ConfiguredProject project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<resources>");
        for (Map.Entry<String, String> entry : component.getStringResources().entrySet()) {
            printWriter.println("    <string name='" + entry.getKey() + "'>" + entry.getValue() + "</string>");
        }
        printWriter.println("</resources>");
        printWriter.println();
    }
}
