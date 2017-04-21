package org.gradle.builds

import junit.framework.AssertionFailedError
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractIntegrationTest extends Specification {
    @Rule
    TemporaryFolder tmpDir = new TemporaryFolder(getRootTempDir())
    static File rootTmpDir
    File projectDir
    File userHomeDir
    BuildLayout build

    static File getRootTempDir() {
        if (rootTmpDir == null) {
            def file = new File("build/tmp/tests").canonicalFile
            file.mkdirs()
            rootTmpDir = file
        }
        return rootTmpDir
    }

    def setup() {
        projectDir = tmpDir.newFolder("generated-root-dir")
        build = new BuildLayout(projectDir)
    }

    void useIsolatedUserHome() {
        userHomeDir = tmpDir.newFolder("user-home")
    }

    File file(String path) {
        return new File(projectDir, path)
    }

    BuildLayout build(File rootDir) {
        return new BuildLayout(rootDir)
    }

    void exeSucceeds(InstalledApp app) {
        build.start(app).waitFor()
    }

    void waitFor(URI uri) {
        def url = uri.toURL()
        while (true) {
            try {
                HttpURLConnection urlConnection = url.openConnection()
                urlConnection.responseCode
                break;
            } catch (ConnectException e) {
                // Ignore
                Thread.sleep(100)
            }
        }
    }

    static class InstalledApp {
        final File binFile

        InstalledApp(File binFile) {
            this.binFile = binFile
        }
    }

    static class CommandHandle {
        final Process process

        CommandHandle(Process process) {
            this.process = process
        }

        void waitFor() {
            process.waitFor()
            if (process.exitValue() != 0) {
                throw new AssertionFailedError("Build failed")
            }
        }

        void kill() {
            process.destroy()
            process.waitFor()
        }
    }

    class BuildLayout {
        final File rootDir

        BuildLayout(File rootDir) {
            this.rootDir = rootDir
        }

        File file(String path) {
            return new File(rootDir, path)
        }

        // TODO - add more checks
        void isBuild() {
            assert rootDir.directory
            assert file("settings.gradle").file
            project(':').isProject()
        }

        ProjectLayout project(String path) {
            if (path == ':') {
                return new ProjectLayout(path, this.rootDir)
            } else {
                return new ProjectLayout(path, new File(rootDir, path.replace(':', '/')))
            }
        }

        InstalledApp app(String path) {
            return new InstalledApp(file(path))
        }

        CommandHandle start(InstalledApp app) {
            return start([app.binFile.absolutePath])
        }

        CommandHandle start(List<String> commandLine) {
            def builder = new ProcessBuilder(commandLine)
            builder.directory(rootDir)
            builder.environment().put("JAVA_HOME", System.getProperty("java.home"))
            builder.inheritIO()
            def process = builder.start()
            return new CommandHandle(process)
        }

        void buildSucceeds(String... tasks) {
            def gradleRunner = GradleRunner.create()
            gradleRunner.withGradleVersion("3.5")
            gradleRunner.withTestKitDir(userHomeDir ?: new File(rootTempDir, "testkit"))
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

        File file(String path) {
            return new File(projectDir, path)
        }

        // TODO - add more checks
        void isProject() {
            assert projectDir.directory
            assert buildFile.file
        }

        // TODO - add more checks
        void isEmptyProject() {
            isProject()
            !buildFile.text.contains('apply plugin')
        }

        // TODO - add more checks
        void hasJavaSource() {
            def packagePath
            if (path == ':') {
                packagePath = ''
            } else {
                packagePath = path.replace(':', '/')
            }

            def srcDir = file("src/main/java/org/gradle/example/${packagePath}")
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".java") }

            def testSrcDir = file("src/test/java/org/gradle/example/${packagePath}")
            assert testSrcDir.directory
            assert testSrcDir.list().findAll { it.endsWith(".java") }
        }

        // TODO - add more checks
        void isJavaProject() {
            isProject()
            assert buildFile.text.contains("apply plugin: 'java'")
            hasJavaSource()
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

        // TODO - add more checks
        void isAndroidProject() {
            isProject()
            assert file("src/main/AndroidManifest.xml").file
            hasJavaSource()
        }

        // TODO - add more checks
        void isAndroidLibrary() {
            isAndroidProject()
            assert buildFile.text.contains("apply plugin: 'com.android.library'")
            assert !buildFile.text.contains("apply plugin: 'com.android.application'")
        }

        // TODO - add more checks
        void isAndroidApplication() {
            isAndroidProject()
            assert !buildFile.text.contains("apply plugin: 'com.android.library'")
            assert buildFile.text.contains("apply plugin: 'com.android.application'")
        }

        // TODO - add more checks
        void isCppProject() {
            isProject()

            def srcDir = file("src/main/cpp")
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".cpp") }
            def headerDir = file("src/main/headers")
            assert headerDir.directory
            assert headerDir.list().findAll { it.endsWith(".h") }
        }

        private File getBuildFile() {
            return file("build.gradle")
        }
    }
}
