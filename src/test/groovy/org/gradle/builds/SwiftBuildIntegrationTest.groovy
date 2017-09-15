package org.gradle.builds

import spock.lang.Unroll

class SwiftBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.3-20170914235708+0000"
    }

    def "can generate single project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
        new File(srcDir, "main.swift").text.contains("AppImpl1_1")
        new File(srcDir, "app_impl1_1.swift").text.contains("AppNoDeps1")

        build.project(":").file("src/test/swift").list() as Set == ["apptest.swift", "appimpl1_1test.swift", "appnodeps1test.swift"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "app_impl1_1.swift", "app_nodeps1.swift"] as Set
        new File(srcDir, "app_impl1_1.swift").text.contains("Core1")

        build.project(":").file("src/test/swift").list() as Set == ["apptest.swift", "appimpl1_1test.swift", "appnodeps1test.swift"] as Set

        build.project(":core1").isSwiftLibrary()
        build.project(":core1").file("src/main/swift").list() as Set == ["core1.swift", "core1_impl1_1.swift", "core1_nodeps1.swift"] as Set
        build.project(":core1").file("src/test/swift").list() as Set == ["core1test.swift", "core1impl1_1test.swift", "core1nodeps1test.swift"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate build with #count projects"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", count as String)

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()
        build.project(":core1").isSwiftLibrary()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        count << [3, 5, 10]
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

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate composite build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2")

        then:
        build.isBuild()

        build.project(":").isSwiftApplication()
        build.project(":core1").isSwiftLibrary()
        def srcDir = build.project(":").file("src/main/swift")
        new File(srcDir, "app_impl1_1.swift").text.contains("Child1_core1")

        def coreSrcDir = build.project(":core1").file("src/main/swift")
        new File(coreSrcDir, "core1_impl1_1.swift").text.contains("Child1_core1")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1_core1").isSwiftLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }
}
