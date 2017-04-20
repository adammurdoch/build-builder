package org.gradle.builds

class AndroidBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        isAndroidProject(":")

        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")
    }

    def "can generate single project build with the specified number of source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        isAndroidProject(":")

        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        isAndroidProject(":")
        isAndroidProject(":core1")

        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        projects << ["2", "5"]
    }

    def "can generate multi-project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--java")

        then:
        build.isBuild()
        isAndroidProject(":")
        build.project(":core1").isJavaLibrary()
        isAndroidProject(":lib1_1")
        isAndroidProject(":lib1_2")

        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")
    }

    def "can generate multi-project build with the specified number of source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        isAndroidProject(":")
        isAndroidProject(":lib1_1")
        isAndroidProject(":lib1_2")
        isAndroidProject(":core1")

        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
