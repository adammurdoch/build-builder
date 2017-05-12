package org.gradle.builds.generators;

import org.gradle.builds.model.AndroidComponent;
import org.gradle.builds.model.AndroidLibrary;
import org.gradle.builds.model.JvmLibrary;
import org.gradle.builds.model.Project;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class AndroidLayoutGenerator extends ProjectComponentSpecificGenerator<AndroidComponent> {
    public AndroidLayoutGenerator() {
        super(AndroidComponent.class);
    }

    @Override
    protected void generate(Project project, AndroidComponent component) throws IOException {
        Path layoutXml = project.getProjectDir().resolve("src/main/res/layout/" + project.getName().toLowerCase() + "_layout.xml");
        Files.createDirectories(layoutXml.getParent());
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(layoutXml))) {
            generate(project, component, printWriter);
        }
    }

    private void generate(Project project, AndroidComponent component, PrintWriter printWriter) {
        printWriter.println("<!-- GENERATED SOURCE FILE -->");
        printWriter.println("<LinearLayout xmlns:android='http://schemas.android.com/apk/res/android'");
        printWriter.println("  android:id='@+id/" + project.getName() + "_layout'");
        printWriter.println("  android:layout_width='match_parent'");
        printWriter.println("  android:layout_height='match_parent'");
        printWriter.println("  android:orientation='vertical' >");
        printWriter.println("    <TextView");
        printWriter.println("      android:id='@+id/textbox'");
        printWriter.println("      android:layout_width='wrap_content'");
        printWriter.println("      android:layout_height='wrap_content'");
        printWriter.println("      android:layout_centerHorizontal='true'");
        printWriter.println("      android:layout_centerVertical='true' />");
        for (Project dep : project.getDependencies()) {
            if (dep.component(AndroidLibrary.class) != null) {
                printWriter.println("    <Button");
                printWriter.println("      android:layout_width='wrap_content'");
                printWriter.println("      android:layout_height='wrap_content'");
                printWriter.println("      android:text='project " + dep.getPath() + "'");
                printWriter.println("      android:onClick='click" + dep.getName() + "' />");
            } else if (dep.component(JvmLibrary.class) != null) {
                printWriter.println("    <TextView");
                printWriter.println("      android:layout_width='wrap_content'");
                printWriter.println("      android:layout_height='wrap_content'");
                printWriter.println("      android:text='project " + dep.getPath() + "' />");
            }
        }
        printWriter.println("</LinearLayout>");
        printWriter.println();
    }
}
