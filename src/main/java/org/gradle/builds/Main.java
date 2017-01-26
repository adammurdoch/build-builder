package org.gradle.builds;

import io.airlift.airline.*;
import org.gradle.builds.assemblers.*;
import org.gradle.builds.generators.*;
import org.gradle.builds.inspectors.BuildInspector;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Project;

import java.io.File;
import java.io.IOException;
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
        cliBuilder.withCommand(AddSource.class);
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

    public static abstract class SourceGenerationCommand implements Callable<Void> {
        @Option(name = "--source-files", description = "The number of source files to generate for each project (default: 3)")
        int sourceFiles = 3;
    }

    @Command(name = "add-source", description = "Generates source files for an existing build")
    public static class AddSource extends SourceGenerationCommand {
        @Option(name = "--dir", description = "The build to add source to (default: current directory)")
        String rootDir = ".";

        @Override
        public Void call() throws Exception {
            if (sourceFiles < 1) {
                throw new IllegalArgumentException("Minimum of 1 source files per project.");
            }

            Path rootDir = new File(this.rootDir).getCanonicalFile().toPath();

            System.out.println("* Adding source to build in " + rootDir);
            System.out.println("* Source files per project: " + sourceFiles);

            Build build = new Build(rootDir);

            // Inspect model
            new BuildInspector(new AllTypesProjectDecorator()).inspect(build);

            System.out.println("* Projects: " + build.getProjects().size());

            for (Project project : build.getProjects()) {
                System.out.println("  * path: " + project.getPath() + " dir: " + rootDir.relativize(project.getProjectDir()));
            }

            return null;
        }
    }

    @Command(name = "init", description = "Generates a build with source files")
    public static class InitBuild extends SourceGenerationCommand {
        @Option(name = "--dir", description = "The directory to generate into (default: current directory)")
        String rootDir = ".";

        @Option(name = "--type", description = "The type of build to generate (android, java, cpp) (default: java)")
        String type = "java";

        @Option(name = "--projects", description = "The number of projects to include in the build (default: 1)")
        int projects = 1;

        @Override
        public Void call() throws Exception {
            ModelAssembler modelAssembler = createModelAssembler();
            Settings settings = createSettings();
            Path rootDir = getRootDir();

            System.out.println("* Generating build in " + rootDir);
            System.out.println("* Build type: " + type);
            System.out.println("* Projects: " + projects);
            System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects * sourceFiles) + ")");

            Build build = new Build(rootDir);

            // Create model
            new StructureAssembler(modelAssembler).populate(settings, build);
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

        private Path getRootDir() throws IOException {
            return new File(rootDir).getCanonicalFile().toPath();
        }

        private Settings createSettings() {
            if (projects < 1) {
                throw new IllegalArgumentException("Minimum of 1 project.");
            }
            if (sourceFiles < 1) {
                throw new IllegalArgumentException("Minimum of 1 source files per project.");
            }
            return new Settings(projects, sourceFiles);
        }

        private ModelAssembler createModelAssembler() {
            switch (type) {
                case "java":
                    return new JavaModelAssembler();
                case "android":
                    return new AndroidModelAssembler();
                case "cpp":
                    return new CppModelAssembler();
                default:
                    throw new CommandLineValidationException("Unknown build type '" + type + "' specified");
            }
        }
    }
}
