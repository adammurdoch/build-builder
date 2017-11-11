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
        build.project(":").file("src/main/headers").list() as Set == ["app.h", "app_defs1.h"] as Set
        def srcDir = build.project(":").file("src/main/cpp")
        srcDir.list() as Set == ["app.cpp", "app_private.h", "appimpl1api.cpp", "appimpl2api.cpp"] as Set
        new File(srcDir, "app.cpp").text.contains("AppImpl1Api")
        new File(srcDir, "appimpl1api.cpp").text.contains("AppImpl2Api")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles as String)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/cpp").list().size() == sourceFiles + 1

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10, 20]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() as Set == ["app.h", "app_defs1.h"] as Set
        def srcDir = build.project(":").file("src/main/cpp")
        srcDir.list() as Set == ["app.cpp", "app_private.h", "appimpl1api.cpp", "appimpl2api.cpp"] as Set
        new File(srcDir, "appimpl1api.cpp").text.contains("Lib1Api")

        build.project(":lib1api").isCppLibrary()
        build.project(":lib1api").file("src/main/public").list() as Set == ["lib1api.h"] as Set
        build.project(":lib1api").file("src/main/headers").list() as Set == ["lib1api_impl.h"] as Set
        build.project(":lib1api").file("src/main/cpp").list() as Set == ["lib1api.cpp", "lib1api_private.h", "lib1apiimpl1api.cpp", "lib1apiimpl2api.cpp"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file
        file("repo/test/lib1api/1.2/lib1api-1.2.pom").file
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() as Set == ["app.h", "app_defs1.h"] as Set
        build.project(":").file("src/main/cpp").list() as Set == ["app.cpp", "app_private.h", "appimpl1api.cpp", "appimpl2api.cpp"] as Set

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.2/testApp-1.2.pom").file

        where:
        projects << ["3", "4", "5", "10", "20"]
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles as String)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/cpp").list().size() == sourceFiles + 1
        build.project(":lib1api1").isCppLibrary()
        build.project(":lib1api1").file("src/main/cpp").list().size() == sourceFiles + 1
        build.project(":lib1api2").isCppLibrary()
        build.project(":lib2api").isCppLibrary()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10, 20]
    }

    def "can generate multi-project build with 4 header files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--header-files", "4")

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() == ["app.h", "app_defs1.h"]
        build.project(":").file("src/main/cpp").list().findAll { it.endsWith('.h') } == ["app_private.h", "app_private_defs1.h"]

        build.project(":lib1api").isCppLibrary()
        build.project(":lib1api").file("src/main/public").list() == ["lib1api.h"]
        build.project(":lib1api").file("src/main/headers").list() == ["lib1api_impl.h", "lib1api_impl_defs1.h"]
        build.project(":lib1api").file("src/main/cpp").list().findAll { it.endsWith('.h') } == ["lib1api_private.h"]

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate multi-project build with 8 header files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--header-files", "8")

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":").file("src/main/headers").list() == ["app.h", "app_defs1.h", "app_defs2.h", "app_defs3.h"]
        build.project(":").file("src/main/cpp").list().findAll { it.endsWith('.h') } == ["app_private.h", "app_private_defs1.h", "app_private_defs2.h", "app_private_defs3.h"]

        build.project(":lib1api").isCppLibrary()
        build.project(":lib1api").file("src/main/public").list() == ["lib1api.h", "lib1api_defs1.h"]
        build.project(":lib1api").file("src/main/headers").list() == ["lib1api_impl.h", "lib1api_impl_defs1.h", "lib1api_impl_defs2.h"]
        build.project(":lib1api").file("src/main/cpp").list().findAll { it.endsWith('.h') } == ["lib1api_private.h", "lib1api_private_defs1.h", "lib1api_private_defs2.h"]

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate multi-project build with #headerFiles header files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--header-files", headerFiles)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":lib1api1").isCppLibrary()
        build.project(":lib1api2").isCppLibrary()
        build.project(":lib2api").isCppLibrary()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        headerFiles << ["6", "10", "20"]
    }

    def "can generate composite build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2")

        then:
        build.isBuild()

        build.project(":").isCppApplication()
        build.project(":lib1api").isCppLibrary()
        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("Child1Lib1Api")

        def coreSrcDir = build.project(":lib1api").file("src/main/cpp")
        new File(coreSrcDir, "lib1apiimpl1api.cpp").text.contains("Child1Lib1Api")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1lib1api").isCppLibrary()

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
        new File(srcDir, "appimpl1api.cpp").text.contains("RepoLib1Api1")
        new File(srcDir, "appimpl1api.cpp").text.contains("RepoLib1Api2")
        new File(srcDir, "appimpl1api.cpp").text.contains("RepoLib2Api")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repolib1api1').isCppLibrary()
        repoBuild.project(':repolib1api2').isCppLibrary()
        repoBuild.project(':repolib2api').isCppLibrary()

        repoBuild.buildSucceeds("installDist")
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repolib2api/1.2/repolib2api-1.2.pom").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repolib2api/1.2/repolib2api-1.2-cpp-api-headers.zip").file

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
