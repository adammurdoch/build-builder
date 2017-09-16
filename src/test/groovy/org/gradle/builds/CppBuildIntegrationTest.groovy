package org.gradle.builds

import spock.lang.Unroll

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.3-20170914235708+0000"
    }

    def "can generate single project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() as Set == ["app.h"] as Set
        def srcDir = build.project(":").file("src/main/cpp")
        srcDir.list() as Set == ["app.cpp", "app_impl1_1.cpp", "app_nodeps1.cpp"] as Set
        new File(srcDir, "app.cpp").text.contains("AppImpl1_1")
        new File(srcDir, "app_impl1_1.cpp").text.contains("AppNoDeps1")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppApplication()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() as Set == ["app.h"] as Set
        def srcDir = build.project(":").file("src/main/cpp")
        srcDir.list() as Set == ["app.cpp", "app_impl1_1.cpp", "app_nodeps1.cpp"] as Set
        new File(srcDir, "app_impl1_1.cpp").text.contains("Core1")

        build.project(":core1").isCppLibrary()
        build.project(":core1").file("src/main/public").list() as Set == ["core1.h"] as Set
        build.project(":core1").file("src/main/headers").list() as Set == ["core1_impl.h"] as Set
        build.project(":core1").file("src/main/cpp").list() as Set == ["core1.cpp", "core1_impl1_1.cpp", "core1_nodeps1.cpp"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file
        file("repo/test/core1/1.2/core1-1.2.pom").file
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() as Set == ["app.h"] as Set
        build.project(":").file("src/main/cpp").list() as Set == ["app.cpp", "app_impl1_1.cpp", "app_nodeps1.cpp"] as Set

        build.project(":lib1_1").isCppLibrary()
        build.project(":lib1_1").file("src/main/public").list() as Set == ["lib1_1.h"] as Set
        build.project(":lib1_1").file("src/main/headers").list() as Set == ["lib1_1_impl.h"] as Set
        build.project(":lib1_1").file("src/main/cpp").list() as Set == ["lib1_1.cpp", "lib1_1_impl1_1.cpp", "lib1_1_nodeps1.cpp"] as Set

        build.project(":core1").isCppLibrary()
        build.project(":core1").file("src/main/public").list() as Set == ["core1.h"] as Set
        build.project(":core1").file("src/main/headers").list() as Set == ["core1_impl.h"] as Set
        build.project(":core1").file("src/main/cpp").list() as Set == ["core1.cpp", "core1_impl1_1.cpp", "core1_nodeps1.cpp"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file
        file("repo/test/core1/1.2/core1-1.2.pom").file

        where:
        projects << ["3", "4", "5"]
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":lib1_1").isCppLibrary()
        build.project(":lib1_2").isCppLibrary()
        build.project(":core1").isCppLibrary()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate composite build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2")

        then:
        build.isBuild()

        build.project(":").isCppApplication()
        build.project(":core1").isCppLibrary()
        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "app_impl1_1.cpp").text.contains("Child1_core1")

        def coreSrcDir = build.project(":core1").file("src/main/cpp")
        new File(coreSrcDir, "core1_impl1_1.cpp").text.contains("Child1_core1")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1_core1").isCppLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()

        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "app_impl1_1.cpp").text.contains("Repo_core1")
        new File(srcDir, "app_impl1_1.cpp").text.contains("Repo_lib1_1")
        new File(srcDir, "app_impl1_1.cpp").text.contains("Repo_lib1_2")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repo_lib1_1').isCppLibrary()
        repoBuild.project(':repo_lib1_2').isCppLibrary()
        repoBuild.project(':repo_core1').isCppLibrary()

        repoBuild.buildSucceeds("installDist")
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.pom").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2-cpp-api-headers.zip").file

        def server = repoBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }
}
