package org.gradle.builds

import spock.lang.Unroll

class SwiftBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.5"
    }

    def "can generate single project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "AppImpl1Api.swift", "AppImpl2Api.swift"] as Set
        new File(srcDir, "main.swift").text.contains("AppImpl1Api()")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let appimpl2api = AppImpl2Api()")

        def testDir = build.project(":").file("src/test/swift")
        testDir.list() as Set == ["AppTest.swift", "AppImpl1ApiTest.swift", "AppImpl2ApiTest.swift"] as Set
        new File(testDir, "AppTest.swift").text.contains("import TestApp")
        new File(testDir, "AppTest.swift").text.contains("let app = App()")

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
        srcDir.list() as Set == ["main.swift", "AppImpl1Api.swift", "AppImpl2Api.swift"] as Set
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Lib1api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let lib1api = Lib1Api()")

        build.project(":").file("src/test/swift").list() as Set == ["AppTest.swift", "AppImpl1ApiTest.swift", "AppImpl2ApiTest.swift"] as Set

        build.project(":lib1api").isSwiftLibrary()
        build.project(":lib1api").file("src/main/swift").list() as Set == ["Lib1Api.swift", "Lib1ApiImpl1Api.swift", "Lib1ApiImpl2Api.swift"] as Set
        build.project(":lib1api").file("src/test/swift").list() as Set == ["Lib1ApiTest.swift", "Lib1ApiImpl1ApiTest.swift", "Lib1ApiImpl2ApiTest.swift"] as Set

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

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        count << [3, 5, 10, 20]
    }

    @Unroll
    def "can generate multi-project build with #count source files"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", count as String)

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()
        build.project(":").file("src/main/swift").list().size() == count
        build.project(":").file("src/test/swift").list().size() == count

        build.project(":lib1api1").isSwiftLibrary()
        build.project(":lib1api1").file("src/main/swift").list().size() == count
        build.project(":lib1api1").file("src/test/swift").list().size() == count

        build.project(":lib1api2").isSwiftLibrary()
        build.project(":lib2api").isSwiftLibrary()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        count << [1, 5, 10, 20]
    }

    def "can generate using Swift PM layout"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--projects", "2", "--swift-pm")

        then:
        build.isBuild()
        build.project(":").isSwiftPMProject()

        build.file("Package.swift").file

        def srcDir = build.file("Sources/testApp")
        srcDir.list() as Set == ["main.swift", "AppImpl1Api.swift", "AppImpl2Api.swift"] as Set
        new File(srcDir, "AppImpl1Api.swift").text.contains("import Lib1api")
        new File(srcDir, "AppImpl1Api.swift").text.contains("let lib1api = Lib1Api()")

        build.file("Tests/testAppTests").list() as Set == ["AppTest.swift", "AppImpl1ApiTest.swift", "AppImpl2ApiTest.swift"] as Set

        build.project(":lib1api").isSwiftPMProject()
        build.file("Sources/lib1api").list() as Set == ["Lib1Api.swift", "Lib1ApiImpl1Api.swift", "Lib1ApiImpl2Api.swift"] as Set

        build.file("Tests/lib1apiTests").list() as Set == ["Lib1ApiTest.swift", "Lib1ApiImpl1ApiTest.swift", "Lib1ApiImpl2ApiTest.swift"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate composite build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--included-builds", "1")

        then:
        build.isBuild()

        def rootProject = build.project(":").isSwiftApplication()

        def child = build(file("child1api"))
        child.isBuild()
        child.project(":").isEmptyProject()
        def lib1 = child.project(":child1apilib1api").isSwiftLibrary()
        def lib2 = child.project(":child1apilib2api").isSwiftLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }
}
