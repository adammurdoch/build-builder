package org.gradle.builds

import junit.framework.AssertionFailedError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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
    String gradleVersion = "3.5"
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
        final BuildLayout owner
        final File binFile

        InstalledApp(BuildLayout owner, File binFile) {
            this.owner = owner
            this.binFile = binFile
        }

        CommandHandle start() {
            assert binFile.isFile()
            return owner.start(this)
        }

        File getLibDir() {
            return new File(binFile.parentFile.parentFile, "lib")
        }

        void isApp() {
            assert binFile.file
            assert libDir.directory
        }

        void succeeds() {
            start().waitFor()
        }
    }

    static class CommandHandle {
        final Process process
        final Thread forwarder

        CommandHandle(Process process, Thread forwarder) {
            this.process = process
            this.forwarder = forwarder
        }

        void waitFor() {
            process.waitFor()
            forwarder.join()
            if (process.exitValue() != 0) {
                throw new AssertionFailedError("Build failed")
            }
        }

        void kill() {
            process.destroy()
            process.waitFor()
            forwarder.join()
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
            def repository = FileRepositoryBuilder.create(file(".git"))
            try {
                def git = new Git(repository)
                def status = git.status().call()
                assert !status.hasUncommittedChanges()
                assert status.untracked.empty
                assert status.untrackedFolders.empty
            } finally {
                repository.close()
            }
        }

        ProjectLayout project(String path) {
            if (path == ':') {
                return new ProjectLayout(path, rootDir, rootDir)
            } else {
                return new ProjectLayout(path, new File(rootDir, path.replace(':', '/')), rootDir)
            }
        }

        InstalledApp app(String path) {
            return new InstalledApp(this, file(path))
        }

        CommandHandle start(InstalledApp app) {
            return start([app.binFile.absolutePath])
        }

        CommandHandle start(List<String> commandLine) {
            def builder = new ProcessBuilder(commandLine)
            builder.directory(rootDir)
            builder.environment().put("JAVA_HOME", System.getProperty("java.home"))
            builder.redirectErrorStream(true)
            def process = builder.start()
            def forwarder = new Thread() {
                @Override
                void run() {
                    def buffer = new byte[1024]
                    while (true) {
                        int nread = process.inputStream.read(buffer)
                        if (nread < 0) {
                            break;
                        }
                        System.out.write(buffer, 0, nread)
                    }
                }
            }
            forwarder.start()
            return new CommandHandle(process, forwarder)
        }

        void buildSucceeds(String... tasks) {
            def gradleRunner = GradleRunner.create()
            gradleRunner.withGradleVersion(gradleVersion)
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
        final File rootDir

        ProjectLayout(String path, File projectDir, File rootDir) {
            this.path = path
            this.projectDir = projectDir
            this.rootDir = rootDir
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
            !buildFile.text.contains('plugin')
        }

        void containsFilesWithExtension(File dir, String extension) {
            assert dir.directory
            List<File> found = []
            dir.eachFileRecurse { f -> if (f.name.endsWith("." + extension)) { found.add(f) } }
            assert !found.empty
        }

        // TODO - add more checks
        void hasJavaSource() {
            def srcDir = file("src/main/java")
            containsFilesWithExtension(srcDir, "java")

            def resourceDir = file("src/main/resources")
            containsFilesWithExtension(resourceDir, "properties")

            def testSrcDir = file("src/test/java")
            containsFilesWithExtension(testSrcDir, "java")
        }

        void appliesPlugin(String plugin) {
            assert buildFile.text.contains("apply plugin: '$plugin'")
        }

        void doesNotApplyPlugin(String plugin) {
            assert !buildFile.text.contains(plugin)
        }

        // TODO - add more checks
        void isJavaProject() {
            isProject()
            appliesPlugin('java')
            hasJavaSource()
        }

        // TODO - add more checks
        void isJavaLibrary() {
            isJavaProject()
            doesNotApplyPlugin('application')
        }

        // TODO - add more checks
        void isJavaApplication() {
            isJavaProject()
            appliesPlugin('application')
        }

        // TODO - add more checks
        void isAndroidProject() {
            isProject()
            assert file("src/main/AndroidManifest.xml").file
            hasJavaSource()
            assert file("src/main/res/values/strings.xml").file
            def layoutDir = file("src/main/res/layout")
            assert layoutDir.directory
            assert layoutDir.list().findAll { it.endsWith(".xml") }

            def testSrcDir = file("src/androidTest/java")
            containsFilesWithExtension(testSrcDir, "java")
        }

        // TODO - add more checks
        void isAndroidLibrary() {
            isAndroidProject()
            appliesPlugin('com.android.library')
            doesNotApplyPlugin('com.android.application')
        }

        // TODO - add more checks
        void isAndroidApplication() {
            isAndroidProject()
            doesNotApplyPlugin('com.android.library')
            appliesPlugin('com.android.application')
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

        void isCppApplication() {
            isCppProject()
            appliesPlugin("cpp-executable")
            doesNotApplyPlugin("cpp-library")
        }

        void isCppLibrary() {
            isCppProject()
            appliesPlugin("cpp-library")
            doesNotApplyPlugin("cpp-executable")
        }

        // TODO - add more checks
        void isSwiftProject() {
            isProject()

            def srcDir = file("src/main/swift")
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".swift") }

            def testDir = file("src/test/swift")
            assert testDir.directory
            assert testDir.list().findAll { it.endsWith(".swift") }
        }

        void isSwiftApplication() {
            isSwiftProject()
            appliesPlugin('swift-executable')
            doesNotApplyPlugin('swift-library')
            def srcDir = file("src/main/swift")
            assert new File(srcDir, "main.swift").file
        }

        void isSwiftLibrary() {
            isSwiftProject()
            appliesPlugin('swift-library')
            doesNotApplyPlugin('swift-executable')
        }

        // TODO - add more checks
        void isSwiftPMProject() {
            isProject()

            def srcDir = new File(rootDir,"Sources/" + (path == ':' ? 'testApp' : path.substring(1)))
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".swift") }

            def testDir = new File(rootDir,"Tests/" + (path == ':' ? 'testApp' : path.substring(1)) + "Tests")
            assert testDir.directory
            assert testDir.list().findAll { it.endsWith(".swift") }
        }

        private File getBuildFile() {
            return file("build.gradle")
        }
    }
}
