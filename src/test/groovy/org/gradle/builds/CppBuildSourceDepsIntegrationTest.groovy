package org.gradle.builds

class CppBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.6-20180129223723+0000"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isCppApplication()
        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("SrcLib1Api")
        new File(srcDir, "appimpl1api.cpp").text.contains("SrcLib2Api")

        def child = build(file("external/source"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":srclib1api").isCppLibrary()
        child.project(":srclib2api").isCppLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
