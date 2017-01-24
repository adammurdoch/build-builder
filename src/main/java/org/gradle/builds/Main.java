package org.gradle.builds;

import io.airlift.airline.*;
import org.gradle.builds.assemblers.*;
import org.gradle.builds.generators.*;
import org.gradle.builds.model.Build;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Callable;

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

    public void run(String[] args) throws Exception {
        Cli.CliBuilder<Callable<Void>> cliBuilder = new Cli.CliBuilder<>("build-builder");
        cliBuilder.withCommand(InitBuild.class);
        cliBuilder.withCommand(Help.class);
        cliBuilder.withDefaultCommand(Help.class);
        Cli<Callable<Void>> cli = cliBuilder.build();

        try {
            cli.parse(args).call();
        } catch (ParseException | CommandLineValidationException e) {
            System.out.println(e.getMessage());
            System.out.println();
            Help.help(cli.getMetadata(), Collections.emptyList());
            throw new SettingsNotAvailableException();
        }
    }

    private static class CommandLineValidationException extends RuntimeException {
        public CommandLineValidationException(String message) {
            super(message);
        }
    }

    private static class SettingsNotAvailableException extends RuntimeException {
    }

    @Command(name = "init", description = "Generates a build with source files")
    public static class InitBuild implements Callable<Void> {
        @Option(name = "--dir", description = "The directory to generate into (default: current directory)")
        String rootDir = ".";

        @Option(name = "--type", description = "The type of build to generate (android, java, cpp) (default: java)")
        String type = "java";

        @Option(name = "--projects", description = "The number of projects to include in the build (default: 1)")
        int projects = 1;

        @Option(name = "--source-files", description = "The number of source files to include in each project (default: 3)")
        int sourceFiles = 3;

        @Override
        public Void call() throws Exception {
            ModelAssembler modelAssembler;
            switch (type) {
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
                    throw new CommandLineValidationException("Unknown build type '" + type + "' specified");
            }

            if (projects < 1) {
                throw new IllegalArgumentException("Minimum of 1 project.");
            }
            if (sourceFiles < 1) {
                throw new IllegalArgumentException("Minimum of 1 source files per project.");
            }
            Settings settings = new Settings(projects, sourceFiles);

            Path projectDir = new File(rootDir).getCanonicalFile().toPath();
            System.out.println("* Generating build in " + projectDir);
            System.out.println("* Build type: " + type);
            System.out.println("* Projects: " + projects);
            System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects * sourceFiles) + ")");

            Build build = new Build(projectDir);

            // Create model
            new StructureAssembler().populate(settings, build);
            modelAssembler.populate(settings, build);

            // Generate files
            new SettingsFileGenerator().generate(build);
            new BuildFileGenerator().generate(build);
            new AndroidManifestGenerator().generate(build);
            new AndroidStringResourcesGenerator().generate(build);
            new JavaSourceGenerator().generate(build);
            new CppSourceGenerator().generate(build);
            new ScenarioFileGenerator().generate(build);

            return null;
        }
    }
}
