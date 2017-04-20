package org.gradle.builds

import junit.framework.AssertionFailedError
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractIntegrationTest extends Specification {
    @Rule
    TemporaryFolder tmpDir = new TemporaryFolder()
    File projectDir
    BuildLayout build

    def setup() {
        projectDir = tmpDir.newFolder("generated-root-dir")
        build = new BuildLayout(projectDir)
    }

    File file(String path) {
        return new File(projectDir, path)
    }

    BuildLayout build(File rootDir) {
        return new BuildLayout(rootDir)
    }

    // TODO - add more checks
    void isAndroidProject(String path) {
        def layout = build.project(path)
        def packagePath
        if (path == ':') {
            packagePath = ''
        } else {
            packagePath = path.replace(':', '/')
        }

        layout.isProject()

        assert new File(layout.projectDir, "src/main/AndroidManifest.xml").file
        def srcDir = new File(layout.projectDir, "src/main/java/org/gradle/example/${packagePath}")
        assert srcDir.directory
        assert srcDir.list().findAll { it.endsWith(".java") }
    }

    // TODO - add more checks
    void isCppProject(String path) {
        def layout = build.project(path)

        layout.isProject()

        def srcDir = new File(layout.projectDir, "src/main/cpp")
        assert srcDir.directory
        assert srcDir.list().findAll { it.endsWith(".cpp") }
        def headerDir = new File(layout.projectDir, "src/main/headers")
        assert headerDir.directory
        assert headerDir.list().findAll { it.endsWith(".h") }
    }

    void buildSucceeds(String... tasks) {
        build.buildSucceeds(tasks)
    }

    void exeSucceeds(File path) {
        runCommand([path.absolutePath])
    }

    private void runCommand(List<String> commandLine) {
        def builder = new ProcessBuilder(commandLine)
        builder.directory(projectDir)
        builder.environment().put("JAVA_HOME", System.getProperty("java.home"))
        builder.environment().put("ANDROID_HOME", System.getProperty("user.home") + "/Library/Android/sdk")
        builder.inheritIO()
        def process = builder.start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw new AssertionFailedError("Build failed")
        }
    }

    static class BuildLayout {
        final File rootDir

        BuildLayout(File rootDir) {
            this.rootDir = rootDir
        }

        // TODO - add more checks
        void isBuild() {
            assert rootDir.directory
            assert new File(rootDir, "settings.gradle").file
            project(':').isProject()
        }

        ProjectLayout project(String path) {
            if (path == ':') {
                return new ProjectLayout(path, this.rootDir)
            } else {
                return new ProjectLayout(path, new File(rootDir, path.replace(':', '/')))
            }
        }

        void buildSucceeds(String... tasks) {
            def gradleRunner = GradleRunner.create()
            gradleRunner.withGradleVersion("3.5")
            gradleRunner.withProjectDir(rootDir)
            gradleRunner.withArguments(["-S"] + (tasks as List))
            gradleRunner.forwardOutput()
            gradleRunner.build()
        }

    }

    static class ProjectLayout {
        final String path
        final File projectDir

        ProjectLayout(String path, File projectDir) {
            this.path = path
            this.projectDir = projectDir
        }

        // TODO - add more checks
        void isProject() {
            assert projectDir.directory
            assert getBuildFile().file
        }

        // TODO - add more checks
        void isEmptyProject() {
            isProject()
            !buildFile.text.contains('apply plugin')
        }

        // TODO - add more checks
        void isJavaProject() {
            def packagePath
            if (path == ':') {
                packagePath = ''
            } else {
                packagePath = path.replace(':', '/')
            }

            isProject()
            assert buildFile.text.contains("apply plugin: 'java'")

            def srcDir = new File(projectDir, "src/main/java/org/gradle/example/${packagePath}")
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".java") }
        }

        // TODO - add more checks
        void isJavaLibrary() {
            isJavaProject()
            assert !buildFile.text.contains("apply plugin: 'application'")
        }

        // TODO - add more checks
        void isJavaApplication() {
            isJavaProject()
            assert buildFile.text.contains("apply plugin: 'application'")
        }

        private File getBuildFile() {
            new File(projectDir, "build.gradle")
        }
    }
}
