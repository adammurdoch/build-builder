package org.gradle.builds

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()

        build.buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        build.buildSucceeds("build")
    }

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()

        build.buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()
        build.project(":core1").isJavaLibrary()

        build.buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        build.buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()
        build.project(":lib1_1").isJavaLibrary()
        build.project(":lib1_2").isJavaLibrary()
        build.project(":core1").isJavaLibrary()

        build.buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate single project build with http repo"() {
        when:
        new Main().run("java", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':lib1_1').isJavaLibrary()
        repoBuild.project(':lib1_2').isJavaLibrary()
        repoBuild.project(':core1').isJavaLibrary()

        repoBuild.buildSucceeds("installDist")
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/ext_core1/1.2/ext_core1-1.2.jar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/ext_core1/1.2/ext_core1-1.2.pom").file

        build.buildSucceeds(":installDist")
        exeSucceeds(file("build/install/testApp/bin/testApp"))

        build.buildSucceeds("build")
    }
}
