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
        cliBuilder.withCommand(InitSwiftBuild.class);
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

            Build build = new Build(rootDir, "testApp");

            // Inspect model
            ModelAssembler modelAssembler = new AllTypesProjectDecorator();
            Settings settings = new Settings(build.getProjects().size(), sourceFiles);
            build.setSettings(settings);
            new BuildInspector().inspect(build);

            System.out.println("* Projects: " + build.getProjects().size());

            new StructureAssembler().arrangeClasses(build);
            modelAssembler.populate(build);

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
            validate();

            Path rootDir = getRootDir();

            System.out.println("* Generating build in " + rootDir);
            System.out.println("* Build type: " + getType());
            System.out.println("* Projects: " + projects);
            System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects * sourceFiles) + ")");

            Settings settings = createSettings();

            Model model = new Model(new Build(rootDir, "testApp"));

            // Create build structure
            createModelStructureAssembler().attachBuilds(settings, model);

            // Configure model
            createModelConfigurer().populate(model);

            // Generate files
            createModelGenerator().generate(model);

            return null;
        }

        protected void validate() {
            if (projects < 1) {
                throw new IllegalArgumentException("Minimum of 1 project.");
            }
            if (sourceFiles < 1) {
                throw new IllegalArgumentException("Minimum of 1 source files per project.");
            }
        }

        private ModelConfigurer createModelConfigurer() {
            return new ModelConfigurer(
                    new InitialProjectSetupBuildConfigurer(
                            new CompositeModelAssembler(
                                    new HttpServerModelAssembler(),
                                    createModelAssembler())));
        }

        private ModelGenerator createModelGenerator() {
            return new ModelGenerator(
                    new CompositeBuildGenerator(
                            new SettingsFileGenerator(),
                            new BuildFileGenerator(),
                            new AndroidManifestGenerator(),
                            new AndroidLocalPropertiesGenerator(),
                            new AndroidStringResourcesGenerator(),
                            new AndroidLayoutGenerator(),
                            new AndroidImageGenerator(),
                            new JavaSourceGenerator(),
                            new JavaResourceGenerator(),
                            new CppSourceGenerator(),
                            new SwiftPackageManagerManifestGenerator(),
                            new SwiftSourceGenerator(),
                            // TODO - remove this
                            new XCTestInfoPlistGenerator(),
                            new HttpServerMainGenerator(),
                            new ScenarioFileGenerator()));
        }

        protected ModelStructureAssembler createModelStructureAssembler() {
            return new SingleBuildModelStructureAssembler(createProjectInitializer());
        }

        private Path getRootDir() throws IOException {
            return new File(rootDir).getCanonicalFile().toPath();
        }

        private Settings createSettings() {
            return new Settings(projects, sourceFiles);
        }

        protected abstract String getType();

        protected abstract ProjectInitializer createProjectInitializer();

        protected abstract ModelAssembler createModelAssembler();
    }

    public static abstract class InitJvmBuild extends InitBuild {
        @Option(name = "--http-repo", description = "Generate an HTTP repository (default: false)")
        boolean httpRepo = false;

        @Override
        protected ModelStructureAssembler createModelStructureAssembler() {
            ModelStructureAssembler mainBuildAssembler = super.createModelStructureAssembler();
            if (httpRepo) {
                return new CompositeModelStructureAssembler(
                        mainBuildAssembler,
                        new HttpRepoModelStructureAssembler(createProjectInitializer()));
            } else {
                return mainBuildAssembler;
            }
        }
    }

    @Command(name = "java", description = "Generates a Java build with source files")
    public static class InitJavaBuild extends InitJvmBuild {
        @Option(name = "--builds", description = "The number of builds to generate (default: 1)")
        int builds = 1;

        @Override
        protected String getType() {
            return "Java";
        }

        @Override
        protected void validate() {
            super.validate();
            if (builds < 1) {
                throw new IllegalArgumentException("Minimum of 1 build.");
            }
        }

        @Override
        protected ModelStructureAssembler createModelStructureAssembler() {
            return new CompositeModelStructureAssembler(
                    super.createModelStructureAssembler(),
                    new IncludedBuildAssembler(
                            createProjectInitializer(),
                            builds));
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new JavaBuildProjectInitializer();
        }

        @Override
        protected ModelAssembler createModelAssembler() {
            return new JavaModelAssembler();
        }
    }

    @Command(name = "cpp", description = "Generates a C++ build with source files")
    public static class InitCppBuild extends InitBuild {
        @Override
        protected String getType() {
            return "C++";
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new CppBuildProjectInitializer();
        }

        @Override
        protected ModelAssembler createModelAssembler() {
            return new CppModelAssembler();
        }
    }

    @Command(name = "swift", description = "Generates a Swift build with source files")
    public static class InitSwiftBuild extends InitBuild {
        @Option(name = "--swift-pm", description = "Use the Swift package manager source layout (default: false)")
        boolean swiftPm = false;

        @Override
        protected String getType() {
            return "Swift";
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new SwiftBuildProjectInitializer(swiftPm);
        }

        @Override
        protected ModelAssembler createModelAssembler() {
            return new SwiftModelAssembler();
        }
    }

    @Command(name = "android", description = "Generates an Android build with source files")
    public static class InitAndroidBuild extends InitJvmBuild {
        @Option(name = "--version", description = "Android plugin version (default: " + AndroidModelAssembler.defaultVersion + ")")
        String androidVersion = AndroidModelAssembler.defaultVersion;

        @Option(name = "--java", description = "Include some Java libraries (default: false)")
        boolean includeJavaLibraries = false;

        @Override
        protected void validate() {
            if (includeJavaLibraries && projects < 3) {
                throw new IllegalArgumentException("Minimum of 3 projects required to add Java libraries to Android build");
            }
        }

        @Override
        protected String getType() {
            return "Android";
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new AndroidBuildProjectInitializer(includeJavaLibraries);
        }

        @Override
        protected ModelAssembler createModelAssembler() {
            AndroidModelAssembler modelAssembler = new AndroidModelAssembler(androidVersion);
            if (includeJavaLibraries) {
                return new CompositeModelAssembler(new JavaModelAssembler(), modelAssembler);
            }
            return modelAssembler;
        }
    }
}
