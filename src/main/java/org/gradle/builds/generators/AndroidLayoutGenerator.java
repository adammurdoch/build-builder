package org.gradle.builds.generators;

import org.gradle.builds.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class AndroidLayoutGenerator extends ProjectComponentSpecificGenerator<AndroidComponent> {
    public AndroidLayoutGenerator() {
        super(AndroidComponent.class);
    }

    @Override
    protected void generate(BuildProjectStructureBuilder build, Project project, AndroidComponent component, FileGenerator fileGenerator) throws IOException {
        Path layoutXml = project.getProjectDir().resolve("src/main/res/layout/" + project.getName().toLowerCase() + "_layout.xml");
        fileGenerator.generate(layoutXml, printWriter -> {
            generate(project, component, printWriter);
        });
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
        for (Dependency<JvmLibraryApi> dependency : component.getReferencedLibraries()) {
            JvmLibraryApi library = dependency.getTarget();
            if (library instanceof AndroidLibraryApi) {
                printWriter.println("    <Button");
                printWriter.println("      android:layout_width='wrap_content'");
                printWriter.println("      android:layout_height='wrap_content'");
                printWriter.println("      android:text='" + library.getDisplayName() + "'");
                printWriter.println("      android:onClick='click" + library.getIdentifier() + "' />");
            } else {
                printWriter.println("    <TextView");
                printWriter.println("      android:layout_width='wrap_content'");
                printWriter.println("      android:layout_height='wrap_content'");
                printWriter.println("      android:text='" + library.getDisplayName() + "' />");
            }
        }
        printWriter.println("</LinearLayout>");
        printWriter.println();
    }
}
