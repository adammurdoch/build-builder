package org.gradle.builds

import junit.framework.AssertionFailedError
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

    void buildSucceeds(String... tasks) {
        def commandLine = ["/Users/adam/gradle/current/bin/gradle", "-S"] + (tasks as List)
        println "RUNNING: " + commandLine
        runCommand(commandLine)
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