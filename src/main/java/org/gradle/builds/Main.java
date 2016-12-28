package org.gradle.builds;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.gradle.builds.assemblers.*;
import org.gradle.builds.generators.*;
import org.gradle.builds.model.Build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run(args);
        } catch (SettingsNotAvailableException e) {
            // Reported
            System.exit(1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(2);
        }

        System.exit(0);
    }

    public boolean run(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> projectOption = parser.accepts("root-dir", "The directory to generate into").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> typeOption = parser.accepts("type", "The build type to generate (java, android, cpp)").withRequiredArg().defaultsTo("java");
        ArgumentAcceptingOptionSpec<String> projectCountOption = parser.accepts("projects", "The number of projects to include in the build").withRequiredArg().defaultsTo("1");
        ArgumentAcceptingOptionSpec<String> sourceFileCountOption = parser.accepts("source-files", "The number of source files to include in each project").withRequiredArg().defaultsTo("3");

        OptionSet parsedOptions;
        try {
            parsedOptions = parser.parse(args);
        } catch (OptionException e) {
            return fail(parser, e.getMessage());
        }

        if (!parsedOptions.has(projectOption)) {
            return fail(parser, "No project directory specified.");
        }

        ModelAssembler modelAssembler;
        switch (parsedOptions.valueOf(typeOption)) {
            case "java":
                modelAssembler = new JavaModelAssembler();
                break;
            case "android":
                modelAssembler = new AndroidModelAssembler();
                break;
            case "cpp":
                modelAssembler = new CppModelAssembler();
                break;
            default:
                return fail(parser, "Unknown build type '" + parsedOptions.valueOf(typeOption) + "' specified");
        }

        int projects = Integer.valueOf(parsedOptions.valueOf(projectCountOption));
        int sourceFiles = Integer.valueOf(parsedOptions.valueOf(sourceFileCountOption));
        if (sourceFiles < 2) {
            throw new IllegalArgumentException("Minimum of 2 source files per project.");
        }
        Settings settings = new Settings(projects, sourceFiles);

        Path projectDir = new File(parsedOptions.valueOf(projectOption)).toPath();
        System.out.println("* Generating build in " + projectDir);
        System.out.println("* Projects: " + projects);
        System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects*sourceFiles) + ")");

        Build build = new Build(projectDir);

        // Create model
        new StructureAssembler().populate(settings, build);
        modelAssembler.populate(settings, build);

        // Generate files
        new SettingsFileGenerator().generate(build);
        new BuildFileGenerator().generate(build);
        new AndroidManifestGenerator().generate(build);
        new JavaSourceGenerator().generate(build);
        new CppSourceGenerator().generate(build);

        return true;
    }

    private boolean fail(OptionParser parser, String message) throws IOException {
        System.out.println(message);
        System.out.println();
        parser.printHelpOn(System.out);
        throw new SettingsNotAvailableException();
    }

    private class SettingsNotAvailableException extends RuntimeException {
    }
}
