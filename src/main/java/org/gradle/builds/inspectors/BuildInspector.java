package org.gradle.builds.inspectors;

import org.gradle.builds.assemblers.ProjectDecorator;
import org.gradle.builds.model.*;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class BuildInspector {
    private final ProjectDecorator decorator;

    public BuildInspector(ProjectDecorator decorator) {
        this.decorator = decorator;
    }

    public void inspect(Build build) throws IOException {
        System.out.println("* Inspecting build");
        Path initScript = Files.createTempFile("init", ".gradle");
        Path dataFile = Files.createTempFile("init", ".txt");
        try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(initScript))) {
            printWriter.println("def f = file('" + dataFile.toUri() + "')");
            printWriter.println("projectsEvaluated {");
            printWriter.println("    rootProject.allprojects.each { p ->");
            printWriter.println("        def t = 'empty'");
            printWriter.println("        if (p.plugins.hasPlugin('com.android.application')) { t = 'android-application' }");
            printWriter.println("        else if (p.plugins.hasPlugin('com.android.library')) { t = 'android-library' }");
            printWriter.println("        else if (p.plugins.hasPlugin('application')) { t = 'java-application' }");
            printWriter.println("        else if (p.plugins.hasPlugin('java')) { t = 'java-library' }");
            printWriter.println("        f << p.path + ',' + t + ',' + p.projectDir + '\\n'");
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
            String type = parts[1];
            String projectDir = parts[2];
            Project project;
            if (!projectPath.equals(":")) {
                project = build.addProject(projectPath, new File(projectDir).toPath());
            } else {
                project = build.getRootProject();
            }
            switch (type) {
                case "android-application":
                    decorator.apply(AndroidApplication.class, project);
                    break;
                case "android-library":
                    decorator.apply(AndroidLibrary.class, project);
                    break;
                case "java-library":
                    decorator.apply(JavaLibrary.class, project);
                    break;
                case "java-application":
                    decorator.apply(JavaApplication.class, project);
                    break;
                case "empty":
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected project type: " + type);
            }
        }

        Files.delete(initScript);
        Files.delete(dataFile);
    }
}
