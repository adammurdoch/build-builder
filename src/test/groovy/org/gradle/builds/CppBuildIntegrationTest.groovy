package org.gradle.builds

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppProject()

        build.buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        build.buildSucceeds("build")
    }

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppProject()

        build.buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        build.project(":").isCppProject()
        build.project(":core1").isCppProject()

        build.buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        build.buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppProject()
        build.project(":lib1_1").isCppProject()
        build.project(":lib1_2").isCppProject()
        build.project(":core1").isCppProject()

        build.buildSucceeds(":installMain")
        exeSucceeds(file("build/install/main/testApp"))

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
