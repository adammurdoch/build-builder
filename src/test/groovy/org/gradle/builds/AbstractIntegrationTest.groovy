package org.gradle.builds

import groovy.io.FileType
import junit.framework.AssertionFailedError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.function.Consumer
import java.util.regex.Pattern

abstract class AbstractIntegrationTest extends Specification {
    @Rule
    TemporaryFolder tmpDir = new TemporaryFolder(getRootTempDir())
    static File rootTmpDir
    File projectDir
    File userHomeDir
    String gradleVersion = "5.0"
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

        void withGit(Consumer<Git> cl) {
            def repository = FileRepositoryBuilder.create(file(".git"))
            try {
                def git = new Git(repository)
                cl.accept(git)
            } finally {
                repository.close()
            }
        }

        void isCleanGitRepo() {
            assert file(".git").directory
            withGit { git ->
                def status = git.status().call()
                assert status.clean
                assert !status.hasUncommittedChanges()
                assert status.untracked.empty
                assert status.untrackedFolders.empty
            }
        }

        // TODO - add more checks
        void isBuild() {
            assert rootDir.directory
            assert file("settings.gradle").file
            rootProject.isProject()
            if (file(".git").directory) {
                isCleanGitRepo()
            }
        }

        ProjectLayout getRootProject() {
            return project(":")
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
        private String name

        ProjectLayout(String path, File projectDir, File rootDir) {
            this.path = path
            this.projectDir = projectDir
            this.rootDir = rootDir
        }

        String getName() {
            if (name == null) {
                if (path == ':') {
                    def settings = file("settings.gradle").text
                    def matcher = Pattern.compile("rootProject.name = '(.+)'").matcher(settings)
                    assert matcher.find()
                    name = matcher.group(1)
                    if (name == 'testApp') {
                        name = 'app'
                    }
                } else {
                    name = path.substring(1)
                }
            }
            return name
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
            assert !buildFile.text.contains('plugin')
        }

        void containsFilesWithExtension(File dir, String extension) {
            assert dir.directory
            List<File> found = []
            dir.eachFileRecurse { f ->
                if (f.name.endsWith("." + extension)) {
                    found.add(f)
                }
            }
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
            hasJavaSource()
        }

        // TODO - add more checks
        JavaProject isJavaLibrary() {
            isJavaProject()
            appliesPlugin('java')
            doesNotApplyPlugin('application')
            return new JavaProject(path, projectDir, rootDir)
        }

        // TODO - add more checks
        JavaProject isJavaApplication() {
            isJavaProject()
            appliesPlugin('application')
            return new JavaProject(path, projectDir, rootDir)
        }

        // TODO - add more checks
        JavaProject isJavaPlugin() {
            isJavaProject()
            appliesPlugin('java-gradle-plugin')
            return new JavaProject(path, projectDir, rootDir)
        }

        void isKotlinApplication() {
            appliesPlugin("kotlin")
            appliesPlugin("application")
        }

        void isKotlinLibrary() {
            appliesPlugin("kotlin")
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
        AndroidProject isAndroidLibrary() {
            isAndroidProject()
            appliesPlugin('com.android.library')
            doesNotApplyPlugin('com.android.application')
            return new AndroidProject(path, projectDir, rootDir)
        }

        // TODO - add more checks
        AndroidProject isAndroidApplication() {
            isAndroidProject()
            doesNotApplyPlugin('com.android.library')
            appliesPlugin('com.android.application')
            return new AndroidProject(path, projectDir, rootDir)
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

        CppProject isCppApplication() {
            isCppProject()
            appliesPlugin("cpp-application")
            doesNotApplyPlugin("cpp-library")
            return new CppProject(path, projectDir, rootDir)
        }

        CppProject isCppLibrary() {
            isCppProject()
            appliesPlugin("cpp-library")
            doesNotApplyPlugin("cpp-application")
            return new CppProject(path, projectDir, rootDir)
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

        SwiftProject isSwiftApplication() {
            isSwiftProject()
            appliesPlugin('swift-application')
            doesNotApplyPlugin('swift-library')
            def srcDir = file("src/main/swift")
            assert new File(srcDir, "main.swift").file
            return new SwiftProject(path, projectDir, rootDir)
        }

        SwiftProject isSwiftLibrary() {
            isSwiftProject()
            appliesPlugin('swift-library')
            doesNotApplyPlugin('swift-application')
            return new SwiftProject(path, projectDir, rootDir)
        }

        // TODO - add more checks
        void isSwiftPMProject() {
            isProject()

            def srcDir = new File(rootDir, "Sources/" + (path == ':' ? 'testApp' : path.substring(1)))
            assert srcDir.directory
            assert srcDir.list().findAll { it.endsWith(".swift") }

            def testDir = new File(rootDir, "Tests/" + (path == ':' ? 'testApp' : path.substring(1)) + "Tests")
            assert testDir.directory
            assert testDir.list().findAll { it.endsWith(".swift") }
        }

        private File getBuildFile() {
            return file("build.gradle")
        }
    }

    static class TestDir extends File {
        TestDir(File parent, String path) {
            super(parent, path)
        }

        File file(String name) {
            return new File(this, name)
        }

        List<File> listAll() {
            List<File> files = []
            eachFileRecurse(FileType.FILES) { files << it }
            return files
        }

        void contains(String... files) {
            assert (listAll().collect { toPath().relativize(it.toPath()).toString() } as Set) == (files as Set)
        }
    }

    static abstract class TypedProjectLayout extends ProjectLayout {
        TypedProjectLayout(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        abstract TestDir getSrc()

        protected File findImplSourceFile(String extension) {
            def files = src.listAll().findAll { it.name.endsWith(extension) }
            def srcFile = files.size() == 1 ? files[0] : files.find { it.name.toLowerCase() == "${name}implapi${extension}" }
            return srcFile
        }

        protected File findApiSourceFile(String extension) {
            def files = src.listAll().findAll { it.name.endsWith(extension) }
            def srcFile = files.size() == 1 ? files[0] : files.find { it.name.toLowerCase() == "${name}${extension}" }
            return srcFile
        }

        protected List<String> extractDependenciesFromBuildScript() {
            def result = []
            def externalDependency = Pattern.compile("(implementation|api|compile)\\s+'org.gradle.example:(.+):(.+)'")
            def matcher = externalDependency.matcher(file("build.gradle").text)
            while (matcher.find()) {
                result.add(matcher.group(2))
            }
            def projectDependency = Pattern.compile("(implementation|api|compile)\\s+project\\(':(.+)'\\)")
            matcher = projectDependency.matcher(file("build.gradle").text)
            while (matcher.find()) {
                result.add(matcher.group(2))
            }
            return result
        }
    }

    static abstract class JvmProject extends TypedProjectLayout {
        JvmProject(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        @Override
        TestDir getSrc() {
            return new TestDir(projectDir, "src/main/java")
        }
    }

    static class JavaProject extends JvmProject {
        JavaProject(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        void dependsOn(JavaProject... projects) {
            def srcFile = findImplSourceFile(".java")
            def srcText = srcFile.text
            def pattern = Pattern.compile("org\\.gradle\\.example\\.(\\w+)\\.(\\w+)\\.getSomeValue\\(\\);")
            def matcher = pattern.matcher(srcText)
            def libs = []
            while (matcher.find()) {
                def packageName = matcher.group(1)
                if (packageName == name) {
                    continue
                }
                def className = matcher.group(2)
                if (className.toLowerCase() != packageName) {
                    continue
                }
                if (!srcText.contains("${packageName}.${className}.INT_CONST")) {
                    continue
                }
                libs << packageName
            }
            assert libs as Set == projects.name as Set
            assert extractDependenciesFromBuildScript() as Set == projects.name as Set
        }
    }

    static class AndroidProject extends JvmProject {
        AndroidProject(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        void dependsOn(JvmProject... projects) {
            def srcFile = findImplSourceFile(".java")
            def srcText = srcFile.text
            def pattern = Pattern.compile("org\\.gradle\\.example\\.(\\w+)\\.(\\w+)\\.getSomeValue\\(\\);")
            def matcher = pattern.matcher(srcText)
            def libs = []
            while (matcher.find()) {
                def packageName = matcher.group(1)
                if (packageName == name) {
                    continue
                }
                def className = matcher.group(2)
                if (!className.toLowerCase().startsWith(packageName)) {
                    continue
                }
                if (!srcText.contains("${packageName}.${className}.INT_CONST")) {
                    continue
                }
                libs << packageName
            }
            assert libs as Set == projects.name as Set
            assert extractDependenciesFromBuildScript() as Set == projects.name as Set
            projects.each { project ->
                if (project instanceof AndroidProject) {
                    assert srcText.contains("${project.name}.R.string.${project.name}_string")
                }
            }
        }
    }

    static class SwiftProject extends TypedProjectLayout {
        SwiftProject(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        @Override
        TestDir getSrc() {
            return new TestDir(projectDir, "src/main/swift")
        }

        String getApiClassName() {
            def sourceText = findApiSourceFile(".swift").text
            def classPattern = Pattern.compile("class\\s+(\\w+)\\s+\\{")
            def classMatcher = classPattern.matcher(sourceText)
            assert classMatcher.find()
            return classMatcher.group(1)
        }

        void dependsOn(SwiftProject... projects) {
            def srcFile = findImplSourceFile(".swift")
            def srcText = srcFile.text
            def pattern = Pattern.compile("(\\w+)\\.doSomething\\(\\)")
            def matcher = pattern.matcher(srcText)
            def libs = []
            while (matcher.find()) {
                def varName = matcher.group(1)
                if (varName.startsWith(name)) {
                    continue
                }
                libs << varName
            }
            assert libs as Set == projects.name as Set
            assert extractDependenciesFromBuildScript() as Set == projects.name as Set
            projects.each { project ->
                assert srcText.contains("import ${project.name.capitalize()}")
                assert srcText.contains("let ${project.name} = ${project.apiClassName}()")
            }
        }
    }

    static class CppProject extends TypedProjectLayout {
        CppProject(String path, File projectDir, File rootDir) {
            super(path, projectDir, rootDir)
        }

        TestDir getHeaders() {
            return new TestDir(projectDir, "src/main/headers")
        }

        TestDir getPublicHeaders() {
            return new TestDir(projectDir, "src/main/public")
        }

        @Override
        TestDir getSrc() {
            return new TestDir(projectDir, "src/main/cpp")
        }

        TestDir getTestHeaders() {
            return new TestDir(projectDir, "src/test/headers")
        }

        TestDir getTestSrc() {
            return new TestDir(projectDir, "src/test/cpp")
        }

        String getApiClassName() {
            def headerText = publicHeaders.file("${name}.h").text
            def classPattern = Pattern.compile("class\\s+(\\w+)\\s+\\{")
            def classMatcher = classPattern.matcher(headerText)
            assert classMatcher.find()
            return classMatcher.group(1)
        }

        void dependsOn(CppProject... projects) {
            def srcFile = findImplSourceFile(".cpp")
            def srcText = srcFile.text
            def pattern = Pattern.compile("(\\w+)\\.doSomething\\(\\);")
            def matcher = pattern.matcher(srcText)
            def libs = []
            while (matcher.find()) {
                def varName = matcher.group(1)
                if (!srcText.contains("#include \"${varName}.h\"")) {
                    continue
                }
                libs << varName
            }
            assert libs as Set == projects.name as Set
            assert extractDependenciesFromBuildScript() as Set == projects.name as Set
            projects.each { project -> assert srcText.contains("$project.apiClassName $project.name;")
            }
        }
    }
}
