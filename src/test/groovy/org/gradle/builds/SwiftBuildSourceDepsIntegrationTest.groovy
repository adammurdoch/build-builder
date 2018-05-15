package org.gradle.builds

class SwiftBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.6"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isSwiftApplication()
        def srcDir = build.project(":").file("src/main/swift")
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Src1apilib1api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let src1apilib1api = Src1ApiLib1Api()")
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Src1apilib2api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let src1apilib2api = Src1ApiLib2Api()")

        def child1 = build(file("external/source1Api"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":src1apilib1api").isSwiftLibrary()
        child1.project(":src1apilib2api").isSwiftLibrary()

        def child2 = build(file("external/source2Api"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        child2.project(":src2apilib1api").isSwiftLibrary()
        child2.project(":src2apilib2api").isSwiftLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
