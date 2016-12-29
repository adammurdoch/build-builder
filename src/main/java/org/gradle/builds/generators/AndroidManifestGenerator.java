package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;

public class AndroidManifestGenerator extends ComponentSpecificProjectFileGenerator<AndroidComponent> {
    public AndroidManifestGenerator() {
        super(AndroidComponent.class, "src/main/AndroidManifest.xml");
    }

    @Override
    protected void generate(Project project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<manifest package='" + component.getPackageName() + "'>");
        printWriter.println("</manifest>");
    }
}
