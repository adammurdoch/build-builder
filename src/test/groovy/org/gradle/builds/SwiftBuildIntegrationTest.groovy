package org.gradle.builds

class SwiftBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.2-20170807012333+0000"
    }

    def "can generate single project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isSwiftProject()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
        new File(srcDir, "main.swift").text.contains("AppImpl1_1")
        new File(srcDir, "app_impl1_1.swift").text.contains("AppNoDeps1")

        build.project(":").file("src/test/swift").list() as Set == ["apptest.swift", "appimpl1_1test.swift", "appnodeps1test.swift"] as Set

        build.buildSucceeds(":installMain")
        build.app("build/install/testApp/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isSwiftProject()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
        new File(srcDir, "app_impl1_1.swift").text.contains("Core1")

        build.project(":").file("src/test/swift").list() as Set == ["apptest.swift", "appimpl1_1test.swift", "appnodeps1test.swift"] as Set

        build.project(":core1").isSwiftProject()
        build.project(":core1").file("src/main/swift").list() as Set == ["core1.swift", "core1_impl1_1.swift", "core1_nodeps1.swift"] as Set
        build.project(":core1").file("src/test/swift").list() as Set == ["core1test.swift", "core1impl1_1test.swift", "core1nodeps1test.swift"] as Set

        build.buildSucceeds(":installMain")
        build.app("build/install/testApp/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate using Swift PM layout"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "2", "--swift-pm")

        then:
        build.isBuild()
        build.project(":").isSwiftPMProject()

        build.file("Package.swift").file

        def srcDir = build.file("Sources/testApp")
        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
        new File(srcDir, "app_impl1_1.swift").text.contains("Core1")

        build.file("Tests/testAppTests").list() as Set == ["apptest.swift", "appimpl1_1test.swift", "appnodeps1test.swift"] as Set

        build.project(":core1").isSwiftPMProject()
        build.file("Sources/core1").list() as Set == ["core1.swift", "core1_impl1_1.swift", "core1_nodeps1.swift"] as Set

        build.file("Tests/core1Tests").list() as Set == ["core1test.swift", "core1impl1_1test.swift", "core1nodeps1test.swift"] as Set

        build.buildSucceeds(":installMain")
        build.app("build/install/testApp/testApp").succeeds()

        build.buildSucceeds("build")
    }
}
