package org.gradle.builds;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.gradle.builds.generators.BuildFileGenerator;
import org.gradle.builds.generators.SettingsFileGenerator;
import org.gradle.builds.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    private boolean run(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> projectOption = parser.accepts("root-dir", "The directory to generate into").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> typeOption = parser.accepts("type", "The build type to generate (java, android, cpp)").withRequiredArg()
                .defaultsTo("java");
        OptionSet parsedOptions;
        try {
            parsedOptions = parser.parse(args);
        } catch (OptionException e) {
            return fail(parser, e.getMessage());
        }

        if (!parsedOptions.has(projectOption)) {
            return fail(parser, "No project directory specified.");
        }
        ModelBuilder modelBuilder;
        switch (parsedOptions.valueOf(typeOption)) {
            case "java":
                modelBuilder = new JavaModelBuilder();
                break;
            case "android":
                modelBuilder = new AndroidModelBuilder();
                break;
            case "cpp":
                modelBuilder = new CppModelBuilder();
                break;
            default:
                return fail(parser, "Unknown build type '" + parsedOptions.valueOf(typeOption) + "' specified");
        }

        Path projectDir = new File(parsedOptions.valueOf(projectOption)).toPath();
        System.out.println("* Generating build in " + projectDir);

        Build build = new Build(projectDir);

        // Model
        modelBuilder.populate(build);

        // Generate
        Files.createDirectories(projectDir);
        new SettingsFileGenerator().generate(build);
        new BuildFileGenerator().generate(build);

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