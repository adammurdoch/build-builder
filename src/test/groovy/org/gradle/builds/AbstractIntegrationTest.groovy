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

    def setup() {
        projectDir = tmpDir.newFolder("generated-root-dir")
    }

    File file(String path) {
        return new File(projectDir, path)
    }

    File projectDir(String path) {
        if (path == ':') {
            return this.projectDir
        } else {
            return file(path.replace(':', '/'))
        }
    }

    // TODO - add more checks
    void isJavaProject(String path) {
        def projectDir = projectDir(path)
        def packagePath
        if (path == ':') {
            packagePath = ''
        } else {
            packagePath = path.replace(':', '/')
        }

        assert projectDir.directory

        def buildFile = new File(projectDir, "build.gradle")
        assert buildFile.file
        assert buildFile.text.contains("apply plugin: 'java'")

        def srcDir = new File(projectDir, "src/main/java/org/gradle/example/${packagePath}")
        assert srcDir.directory
        assert srcDir.list().findAll { it.endsWith(".java") }
    }

    // TODO - add more checks
    void isAndroidProject(String path) {
        def projectDir = projectDir(path)
        def packagePath
        if (path == ':') {
            packagePath = ''
        } else {
            packagePath = path.replace(':', '/')
        }
        assert projectDir.directory
        assert new File(projectDir, "build.gradle").file
        assert new File(projectDir, "src/main/AndroidManifest.xml").file
        def srcDir = new File(projectDir, "src/main/java/org/gradle/example/${packagePath}")
        assert srcDir.directory
        assert srcDir.list().findAll { it.endsWith(".java") }
    }

    // TODO - add more checks
    void isCppProject(String path) {
        def projectDir = projectDir(path)
        assert projectDir.directory
        assert new File(projectDir, "build.gradle").file
        def srcDir = new File(projectDir, "src/main/cpp")
        assert srcDir.directory
        assert srcDir.list().findAll { it.endsWith(".cpp") }
        def headerDir = new File(projectDir, "src/main/headers")
        assert headerDir.directory
        assert headerDir.list().findAll { it.endsWith(".h") }
    }

    void buildSucceeds(String... tasks) {
        def gradleRunner = GradleRunner.create()
        gradleRunner.withGradleVersion("3.5")
        gradleRunner.withProjectDir(projectDir)
        gradleRunner.withArguments(["-S"] + (tasks as List))
        gradleRunner.forwardOutput()
        gradleRunner.build()
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
}
