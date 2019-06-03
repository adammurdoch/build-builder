package org.gradle.builds

import spock.lang.Unroll

class SwiftBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "5.4.1"
    }

    def "can generate single project build"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isSwiftApplication()

        def srcDir = build.project(":").file("src/main/swift")
        srcDir.list() as Set == ["main.swift", "AppImplApi.swift", "AppImplCore.swift"] as Set
        new File(srcDir, "main.swift").text.contains("AppImplApi()")
        new File(srcDir, "AppImplApi.swift").text.contains("let appimplcore = AppImplCore()")

        def testDir = build.project(":").file("src/test/swift")
        testDir.list() as Set == ["AppTest.swift", "AppImplApiTest.swift", "AppImplCoreTest.swift"] as Set
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
        srcDir.list() as Set == ["main.swift", "AppImplApi.swift", "AppImplCore.swift"] as Set
        new File(srcDir, "AppImplApi.swift").text.contains("import Lib")
        new File(srcDir, "AppImplApi.swift").text.contains("let lib = Lib()")

        build.project(":").file("src/test/swift").list() as Set == ["AppTest.swift", "AppImplApiTest.swift", "AppImplCoreTest.swift"] as Set

        build.project(":lib").isSwiftLibrary()
        build.project(":lib").file("src/main/swift").list() as Set == ["Lib.swift", "LibImplApi.swift", "LibImplCore.swift"] as Set
        build.project(":lib").file("src/test/swift").list() as Set == ["LibTest.swift", "LibImplApiTest.swift", "LibImplCoreTest.swift"] as Set

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

        build.project(":libapi1").isSwiftLibrary()
        build.project(":libapi1").file("src/main/swift").list().size() == count
        build.project(":libapi1").file("src/test/swift").list().size() == count

        build.project(":libapi2").isSwiftLibrary()
        build.project(":libcore").isSwiftLibrary()

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
        srcDir.list() as Set == ["main.swift", "AppImplApi.swift", "AppImplCore.swift"] as Set
        new File(srcDir, "AppImplApi.swift").text.contains("import Lib")
        new File(srcDir, "AppImplApi.swift").text.contains("let lib = Lib()")

        build.file("Tests/testAppTests").list() as Set == ["AppTest.swift", "AppImplApiTest.swift", "AppImplCoreTest.swift"] as Set

        build.project(":lib").isSwiftPMProject()
        build.file("Sources/lib").list() as Set == ["Lib.swift", "LibImplApi.swift", "LibImplCore.swift"] as Set

        build.file("Tests/libTests").list() as Set == ["LibTest.swift", "LibImplApiTest.swift", "LibImplCoreTest.swift"] as Set

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

        def child = build(file("child"))
        child.isBuild()
        child.project(":").isEmptyProject()
        def lib1 = child.project(":childlibapi").isSwiftLibrary()
        def lib2 = child.project(":childlibcore").isSwiftLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }
}
