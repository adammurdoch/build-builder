package org.gradle.builds

class AndroidBuildSourceDepsIntegrationTest extends AbstractAndroidIntegrationTest {
    def setup() {
        gradleVersion = "4.6"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib1api.Src1ApiLib1ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib1api.Src1ApiLib1ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib1api.R.string.src1apilib1api_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib2api.Src1ApiLib2ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib2api.Src1ApiLib2ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib2api.R.string.src1apilib2api_string")

        def child1 = build(file("external/source1Api"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":src1apilib1api").isAndroidLibrary()
        child1.project(":src1apilib2api").isAndroidLibrary()

        def child2 = build(file("external/source2Api"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        child2.project(":src2apilib1api").isAndroidLibrary()
        child2.project(":src2apilib2api").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

}
