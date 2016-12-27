package org.gradle.builds

class AndroidBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "android")

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("--root-dir", projectDir.absolutePath, "--type", "android", "--projects", "5")

        then:
        buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        buildSucceeds("build")
    }
}
