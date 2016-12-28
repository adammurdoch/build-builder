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

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "java", "--source-files", sourceFiles)

        then:
        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "java", "--projects", projects)

        then:
        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "java", "--projects", "4", "--source-files", sourceFiles)

        then:
        buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        buildSucceeds("build")

        where:
        sourceFiles << ["2", "5"]
    }
}
