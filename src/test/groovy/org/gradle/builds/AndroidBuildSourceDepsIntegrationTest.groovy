package org.gradle.builds

class AndroidBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.5-20171218235901+0000"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2", "--version", "3.0.0")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib1api.SrcLib1ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib1api.SrcLib1ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib1api.R.string.srclib1api_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib2api.SrcLib2ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib2api.SrcLib2ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib2api.R.string.srclib2api_string")

        def child = build(file("external/source"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":srclib1api").isAndroidLibrary()
        child.project(":srclib2api").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

}
