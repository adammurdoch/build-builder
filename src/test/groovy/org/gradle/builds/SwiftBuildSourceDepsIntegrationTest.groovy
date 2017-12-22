package org.gradle.builds

class SwiftBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.5-20171218235901+0000"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--source-dep-libraries", "2")

        then:
        build.isBuild()

        build.project(":").isSwiftApplication()
        def srcDir = build.project(":").file("src/main/swift")
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Srclib1api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let srclib1api = SrcLib1Api()")
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Srclib2api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let srclib2api = SrcLib2Api()")

        def child = build(file("external/source"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":srclib1api").isSwiftLibrary()
        child.project(":srclib2api").isSwiftLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
