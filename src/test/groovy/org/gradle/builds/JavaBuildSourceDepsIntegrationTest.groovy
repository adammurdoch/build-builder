package org.gradle.builds

class JavaBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.5-20171218235901+0000"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib1api.SrcLib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib1api.SrcLib1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib2api.SrcLib2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.srclib2api.SrcLib2Api.INT_CONST")

        def child = build(file("external/source"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":srclib1api").isJavaLibrary()
        child.project(":srclib2api").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["srclib1api-1.0.jar", "srclib2api-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
