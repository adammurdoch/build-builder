package org.gradle.builds

class SwiftBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.1-rc-1"
    }

    def "can generate single project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
//        build.project(":").isSwiftProject()
//        def srcDir = build.project(":").file("src/main/swift")
//        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
//        new File(srcDir, "main.swift").text.contains("AppImpl1_1")
//        new File(srcDir, "app_impl1_1.swift").text.contains("AppNoDeps1")

//        build.buildSucceeds(":installMain")
//        build.app("build/install/testApp/testApp").succeeds()

        build.buildSucceeds("build")
    }
}
