package org.gradle.builds;

import io.airlift.airline.*;
import org.gradle.builds.assemblers.*;
import org.gradle.builds.generators.*;
import org.gradle.builds.inspectors.BuildInspector;
import org.gradle.builds.model.Build;
import org.gradle.builds.model.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
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
        cliBuilder.withCommand(InitJavaBuild.class);
        cliBuilder.withCommand(InitCppBuild.class);
        cliBuilder.withCommand(InitAndroidBuild.class);
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
            ModelAssembler modelAssembler = new AllTypesProjectDecorator();
            Settings settings = new Settings(build.getProjects().size(), sourceFiles);
            new BuildInspector(modelAssembler).inspect(build);

            System.out.println("* Projects: " + build.getProjects().size());

            new StructureAssembler(modelAssembler).arrangeClasses(settings, build);
            modelAssembler.populate(settings, build);

            new AndroidStringResourcesGenerator().generate(build);
            new JavaSourceGenerator().generate(build);
            new CppSourceGenerator().generate(build);

            return null;
        }
    }

    public static abstract class InitBuild extends SourceGenerationCommand {
        @Option(name = "--dir", description = "The directory to generate into (default: current directory)")
        String rootDir = ".";

        @Option(name = "--projects", description = "The number of projects to include in the build (default: 1)")
        int projects = 1;

        @Override
        public Void call() throws Exception {
            Path rootDir = getRootDir();

            System.out.println("* Generating build in " + rootDir);
            System.out.println("* Build type: " + getType());
            System.out.println("* Projects: " + projects);
            System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects * sourceFiles) + ")");

            ModelAssembler modelAssembler = createModelAssembler();
            Settings settings = createSettings();

            Build build = new Build(rootDir);
            Model model = new Model(build);

            // Create model
            createModelStructureAssembler().attachBuilds(model);
            StructureAssembler structureAssembler = new StructureAssembler(modelAssembler);
            structureAssembler.arrangeProjects(settings, build);
            structureAssembler.arrangeClasses(settings, build);
            modelAssembler.populate(settings, build);

            // Generate files
            new ModelGenerator(createBuildGenerator()).generate(model);

            return null;
        }

        private BuildGenerator createBuildGenerator() {
            return new CompositeBuildGenerator(Arrays.asList(
                    new SettingsFileGenerator(),
                    new BuildFileGenerator(),
                    new AndroidManifestGenerator(),
                    new AndroidStringResourcesGenerator(),
                    new JavaSourceGenerator(),
                    new CppSourceGenerator(),
                    new ScenarioFileGenerator()
            ));
        }

        protected ModelStructureAssembler createModelStructureAssembler() {
            return new SingleBuildModelStructureAssembler();
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

        protected abstract String getType();

        protected abstract ModelAssembler createModelAssembler();
    }

    @Command(name = "java", description = "Generates a Java build with source files")
    public static class InitJavaBuild extends InitBuild {
        @Option(name = "--http-repo", description = "Generate an HTTP repository (default: false)")
        boolean httpRepo = false;

        @Override
        protected String getType() {
            return "Java";
        }

        protected ModelAssembler createModelAssembler() {
            return new JavaModelAssembler();
        }

        @Override
        protected ModelStructureAssembler createModelStructureAssembler() {
            return httpRepo ? new HttpRepoModelStructureAssembler() : super.createModelStructureAssembler();
        }
    }

    @Command(name = "cpp", description = "Generates a C++ build with source files")
    public static class InitCppBuild extends InitBuild {
        @Override
        protected String getType() {
            return "C++";
        }

        protected ModelAssembler createModelAssembler() {
            return new CppModelAssembler();
        }
    }

    @Command(name = "android", description = "Generates an Android build with source files")
    public static class InitAndroidBuild extends InitBuild {
        @Option(name = "--experimental", description = "Use the experimental Android plugin (default: false")
        boolean experimentalAndroid = false;

        @Option(name = "--java", description = "Include some Java libraries (default: false")
        boolean includeJavaLibraries = false;

        @Override
        protected String getType() {
            return "Android";
        }

        protected ModelAssembler createModelAssembler() {
            return new AndroidModelAssembler(experimentalAndroid, includeJavaLibraries);
        }
    }
}
