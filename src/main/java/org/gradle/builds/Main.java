package org.gradle.builds;

import io.airlift.airline.*;
import org.gradle.builds.assemblers.*;
import org.gradle.builds.generators.*;
import org.gradle.builds.model.BuildTreeBuilder;
import org.gradle.builds.model.MacroIncludes;
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

    public static abstract class InitBuild extends SourceGenerationCommand {
        @Option(name = "--dir", description = "The directory to generate into (default: current directory)")
        String rootDir = ".";

        @Option(name = "--projects", description = "The number of projects to include in the build (default: 1)")
        int projects = 1;

        @Option(name = "--included-builds", description = "The number of included builds to generate (default: 0)")
        int builds = 0;

        @Option(name = "--source-dep-builds", description = "The number of source dependency builds to generate (default: 0)")
        int sourceDeps = 0;

        @Override
        public Void call() throws Exception {
            ProblemCollector collector = new ProblemCollector();
            validate(collector);
            collector.assertNoIssues();

            Path rootDir = getRootDir();

            System.out.println("* Generating build in " + rootDir);
            System.out.println("* Build type: " + getType());
            System.out.println("* Projects: " + projects);
            System.out.println("* Source files per project: " + sourceFiles + " (total: " + (projects * sourceFiles) + ")");

            Settings settings = createSettings();

            // Create build tree
            BuildTreeBuilder buildTree = new BuildTreeBuilder(rootDir);
            createModelStructureAssembler().attachBuilds(settings, buildTree);

            // Configure projects
            Model model = buildTree.toModel();
            createModelConfigurer().populate(model);

            // Generate files
            createModelGenerator().generate(model, new CollectingFileGenerator());

            return null;
        }

        protected int getBuilds() {
            return builds;
        }

        protected boolean isHttpRepo() {
            return false;
        }

        protected int getHttpRepoLibraries() {
            return 0;
        }

        protected int getHttpRepoVersions() {
            return 1;
        }

        protected void validate(ProblemCollector collector) {
            if (projects < 1) {
                collector.problem("Minimum of 1 project required.");
            }
            if (sourceFiles < 1) {
                collector.problem("Minimum of 1 source files per project required.");
            }
        }

        private ModelConfigurer createModelConfigurer() {
            return new ModelConfigurer(
                    new InitialProjectSetupBuildConfigurer(
                            new ProjectDepOrderBuildConfigurer(
                                    new CompositeProjectConfigurer(
                                            new AttachDependenciesConfigurer(),
                                            new HttpServerModelAssembler(),
                                            createModelAssembler()))));
        }

        private Generator<Model> createModelGenerator() {
            return new CompositeGenerator<>(
                    new DotGenerator(),
                    new ModelGenerator(
                            new CompositeGenerator<>(
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
                                    new ReadmeGenerator(),
                                    new ScenarioFileGenerator())),
                    // Should be last to collect all generated files
                    new GitRepoGenerator());
        }

        private BuildTreeAssembler createModelStructureAssembler() {
            ProjectInitializer projectInitializer = createProjectInitializer();
            BuildTreeAssembler mainBuildAssembler = new CompositeModelStructureAssembler(
                    new MainBuildModelStructureAssembler(projectInitializer),
                    new IncludedBuildAssembler(projectInitializer,
                            getBuilds()),
                    new SourceDependencyBuildAssembler(projectInitializer, sourceDeps));
            if (isHttpRepo()) {
                return new CompositeModelStructureAssembler(
                        mainBuildAssembler,
                        new HttpRepoModelStructureAssembler(projectInitializer, getHttpRepoLibraries(), getHttpRepoVersions()));
            } else {
                return mainBuildAssembler;
            }
        }

        private Path getRootDir() throws IOException {
            return new File(rootDir).getCanonicalFile().toPath();
        }

        protected Settings createSettings() {
            return new Settings(projects, sourceFiles);
        }

        protected abstract String getType();

        protected abstract ProjectInitializer createProjectInitializer();

        protected abstract ProjectConfigurer createModelAssembler();
    }

    public static abstract class InitBinaryDependencyAwareBuild extends InitBuild {
        @Option(name = "--http-repo", description = "Generate an HTTP repository (default: false)")
        boolean httpRepo = false;

        @Option(name = "--http-repo-libraries", description = "Number of libraries to include in the HTTP repository (default: 3)")
        int httpRepoLibraries = 3;

        @Option(name = "--http-repo-versions", description = "Number of versions of each library to include in the HTTP repository (default: 1)")
        int httpRepoVersions = 1;

        @Override
        protected boolean isHttpRepo() {
            return httpRepo;
        }

        protected int getHttpRepoLibraries() {
            return httpRepoLibraries;
        }

        @Override
        protected int getHttpRepoVersions() {
            return httpRepoVersions;
        }

        @Override
        protected void validate(ProblemCollector collector) {
            super.validate(collector);
            if (httpRepoLibraries < 1) {
                collector.problem("Minimum of 1 HTTP repository library required.");
            }
            if (httpRepoVersions < 1) {
                collector.problem("Minimum of 1 HTTP repository library version required.");
            }
        }
    }

    @Command(name = "java", description = "Generates a Java build with source files")
    public static class InitJavaBuild extends InitBinaryDependencyAwareBuild {
        @Override
        protected String getType() {
            return "Java";
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new JavaBuildProjectInitializer();
        }

        @Override
        protected ProjectConfigurer createModelAssembler() {
            return new JavaModelAssembler();
        }
    }

    @Command(name = "cpp", description = "Generates a C++ build with source files")
    public static class InitCppBuild extends InitBinaryDependencyAwareBuild {
        @Option(name = "--header-files", description = "The number of header files to generate for each project (default: 3)")
        int headers = 3;

        @Option(name = "--macro-include", description = "Specifies how headers files should reference other header files (values: none, simple, complex. default: none)")
        MacroIncludes macroIncludes = MacroIncludes.none;

        @Option(name = "--boost", description = "Include reference to boost libraries (default: false)")
        boolean boost;

        @Override
        protected void validate(ProblemCollector collector) {
            super.validate(collector);
            if (headers < 3) {
                collector.problem("Minimum of 3 header files per project.");
            }
        }

        @Override
        protected Settings createSettings() {
            return new CppSettings(projects, sourceFiles, headers, macroIncludes, boost);
        }

        @Override
        protected String getType() {
            return "C++";
        }

        @Override
        protected ProjectInitializer createProjectInitializer() {
            return new CppBuildProjectInitializer();
        }

        @Override
        protected ProjectConfigurer createModelAssembler() {
            return new CppModelAssembler(headers, macroIncludes, boost);
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
        protected ProjectConfigurer createModelAssembler() {
            return new SwiftModelAssembler();
        }
    }

    @Command(name = "android", description = "Generates an Android build with source files")
    public static class InitAndroidBuild extends InitBinaryDependencyAwareBuild {
        @Option(name = "--version", description = "Android plugin version (default: " + AndroidModelAssembler.defaultVersion + ")")
        String androidVersion = AndroidModelAssembler.defaultVersion;

        @Option(name = "--java", description = "Include some Java libraries (default: false)")
        boolean includeJavaLibraries = false;

        @Override
        protected void validate(ProblemCollector collector) {
            super.validate(collector);
            if (includeJavaLibraries && projects < 2) {
                collector.problem("Minimum of 2 projects required to add Java libraries to Android build");
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
        protected ProjectConfigurer createModelAssembler() {
            AndroidModelAssembler projectConfigurer = new AndroidModelAssembler(androidVersion);
            if (includeJavaLibraries) {
                return new CompositeProjectConfigurer(new JavaModelAssembler(), projectConfigurer);
            }
            return projectConfigurer;
        }
    }
}
