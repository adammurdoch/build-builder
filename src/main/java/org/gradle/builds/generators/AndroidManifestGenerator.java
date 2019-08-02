package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidApplication;
import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.ConfiguredProject;
import org.gradle.builds.model.JavaClass;

import java.io.PrintWriter;

public class AndroidManifestGenerator extends ProjectComponentSpecificSingleFileGenerator<AndroidComponent> {
    public AndroidManifestGenerator() {
        super(AndroidComponent.class, "src/main/AndroidManifest.xml");
    }

    @Override
    protected void generate(ConfiguredProject project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<manifest xmlns:android='http://schemas.android.com/apk/res/android'");
        printWriter.println("        package='" + component.getPackageName() + "'>");
        printWriter.println("  <application");
        if (component.getLabelResource() != null) {
            printWriter.println("    android:label='@string/" + component.getLabelResource() + "'");
        }
        if (component instanceof AndroidApplication) {
            printWriter.println("    android:icon='@mipmap/ic_launcher'");
        }
        printWriter.println("  >");
        for (JavaClass javaClass : component.getActivities()) {
            printWriter.println("    <activity android:name='" + javaClass.getName() + "'>");
            if (component instanceof AndroidApplication) {
                printWriter.println("      <intent-filter>");
                printWriter.println("        <action android:name='android.intent.action.MAIN'/>");
                printWriter.println("        <category android:name='android.intent.category.LAUNCHER'/>");
                printWriter.println("      </intent-filter>");
            }
            printWriter.println("    </activity>");
        }
        printWriter.println("  </application>");
        printWriter.println("</manifest>");
    }
}
