package org.gradle.builds

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "cpp")

        then:
        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "cpp", "--projects", "5")

        then:
        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")
    }
}
