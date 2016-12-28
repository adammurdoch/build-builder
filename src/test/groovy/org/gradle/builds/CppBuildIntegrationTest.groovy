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

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "cpp", "--source-files", sourceFiles)

        then:
        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "cpp", "--projects", projects)

        then:
        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "cpp", "--projects", "4", "--source-files", sourceFiles)

        then:
        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["2", "5"]
    }
}
