package org.gradle.builds

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath)

        then:
        isJavaProject(":")

        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")
    }

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        isJavaProject(":")

        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        isJavaProject(":")
        isJavaProject(":core1")

        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        isJavaProject(":")
        isJavaProject(":lib1_1")
        isJavaProject(":lib1_2")
        isJavaProject(":core1")

        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
