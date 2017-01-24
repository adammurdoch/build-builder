package org.gradle.builds

class AndroidBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("init", "--dir", projectDir.absolutePath, "--type", "android")

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")
    }

    def "can generate single project build with the specified number of source files"() {
        when:
        new Main().run("init", "--dir", projectDir.absolutePath, "--type", "android", "--source-files", sourceFiles)

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("init", "--dir", projectDir.absolutePath, "--type", "android", "--projects", projects)

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        projects << ["2", "5"]
    }

    def "can generate multi-project build with the specified number of source files"() {
        when:
        new Main().run("init", "--dir", projectDir.absolutePath, "--type", "android", "--projects", "4", "--source-files", sourceFiles)

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
