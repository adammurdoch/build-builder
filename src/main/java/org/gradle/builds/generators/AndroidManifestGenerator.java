package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.JavaClass;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;

public class AndroidManifestGenerator extends ComponentSpecificProjectFileGenerator<AndroidComponent> {
    public AndroidManifestGenerator() {
        super(AndroidComponent.class, "src/main/AndroidManifest.xml");
    }

    @Override
    protected void generate(Project project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<manifest xmlns:android='http://schemas.android.com/apk/res/android'");
        printWriter.println("        package='" + component.getPackageName() + "'>");
        printWriter.println("  <application>");
        for (JavaClass javaClass : component.getActivities()) {
            printWriter.println("    <activity android:name='" + javaClass.getName() + "'>");
            printWriter.println("    </activity>");
        }
        printWriter.println("  </application>");
        printWriter.println("</manifest>");
    }
}
