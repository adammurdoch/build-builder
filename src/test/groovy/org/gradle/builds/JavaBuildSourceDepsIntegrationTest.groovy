package org.gradle.builds

class JavaBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.6"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib1api.Src1ApiLib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.src1apilib1api.Src1ApiLib1Api.INT_CONST")

        def child1 = build(file("external/source1Api"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":src1apilib1api").isJavaLibrary()
        child1.project(":src1apilib2api").isJavaLibrary()

        def child2 = build(file("external/source2Api"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        child2.project(":src2apilib1api").isJavaLibrary()
        child2.project(":src2apilib2api").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == [
                "src1apilib1api-1.0.0.jar",
                "src1apilib2api-1.0.0.jar",
                "src2apilib1api-1.0.0.jar",
                "src2apilib2api-1.0.0.jar",
                "slf4j-api-1.7.25.jar",
                "testApp.jar"
        ] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
