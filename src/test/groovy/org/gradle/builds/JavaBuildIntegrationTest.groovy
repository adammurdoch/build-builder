package org.gradle.builds

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "java")

        then:
        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "java", "--projects", "5")

        then:
        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")
    }
}
