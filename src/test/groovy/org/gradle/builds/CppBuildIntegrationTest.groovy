package org.gradle.builds

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        isCppProject(":")

        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")
    }

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        isCppProject(":")

        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        isCppProject(":")
        isCppProject(":core1")

        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        isCppProject(":")
        isCppProject(":lib1_1")
        isCppProject(":lib1_2")
        isCppProject(":core1")

        buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
