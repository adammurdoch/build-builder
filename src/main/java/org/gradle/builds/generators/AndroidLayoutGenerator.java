package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.Project;

import java.io.PrintWriter;
import java.util.Map;

public class AndroidLayoutGenerator extends ProjectComponentSpecificSingleFileGenerator<AndroidComponent> {
    public AndroidLayoutGenerator() {
        super(AndroidComponent.class, "src/main/res/layout/main_layout.xml");
    }

    @Override
    protected void generate(Project project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<RelativeLayout xmlns:android='http://schemas.android.com/apk/res/android'");
        printWriter.println("  android:layout_width='match_parent'");
        printWriter.println("  android:layout_height='match_parent'>");
        printWriter.println("    <TextView");
        printWriter.println("      android:id='@+id/textbox'");
        printWriter.println("      android:layout_width='wrap_content'");
        printWriter.println("      android:layout_height='wrap_content'");
        printWriter.println("      android:layout_centerHorizontal='true'");
        printWriter.println("      android:layout_centerVertical='true' />");
        printWriter.println("</RelativeLayout>");
        printWriter.println();
    }
}
