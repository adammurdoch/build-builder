package org.gradle.builds.inspectors;

import org.gradle.builds.model.Build;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class BuildInspector {
    public void inspect(Build build) throws IOException {
        System.out.println("* Inspecting build");
        Path initScript = Files.createTempFile("init", ".gradle");
        Path dataFile = Files.createTempFile("init", ".txt");
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(initScript))) {
            printWriter.println("def f = file('" + dataFile.toUri() + "')");
            printWriter.println("rootProject {");
            printWriter.println("    allprojects.each { p ->");
            printWriter.println("        f << p.path + ',' + p.projectDir + '\\n'");
            printWriter.println("    }");
            printWriter.println("}");
        }
        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(build.getRootDir().toFile()).connect();
        try {
            connection.newBuild().withArguments("-I", initScript.toString()).run();
        } finally {
            connection.close();
        }

        for (String line : Files.readAllLines(dataFile)) {
            String[] parts = line.split(",");
            String projectPath = parts[0];
            String projectDir = parts[1];
            if (!projectPath.equals(":")) {
                build.addProject(projectPath, new File(projectDir).toPath());
            }
        }

        Files.delete(initScript);
        Files.delete(dataFile);
    }
}
