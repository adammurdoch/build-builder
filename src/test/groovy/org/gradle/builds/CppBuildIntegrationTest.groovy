package org.gradle.builds

import spock.lang.Unroll

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.8-20180507235951+0000"
    }

    def "can generate single project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()

        rootProject.headers.contains("app.h", "app_defs1.h")

        def src = rootProject.src
        src.contains("app.cpp", "app_private.h", "appimplapi.cpp", "appimplcore.cpp")
        src.file("app.cpp").text.contains("AppImplApi appimplapi;")
        src.file("appimplapi.cpp").text.contains("AppImplCore appimplcore;")

        def testHeaderDir = rootProject.testHeaders
        testHeaderDir.contains("app_test.h")

        rootProject.testSrc.contains("test_main.cpp", "app_test.cpp", "appimplcore_test.cpp", "appimplapi_test.cpp")

        rootProject.dependsOn()

        rootProject.file("performance.scenarios").text.contains('apply-h-change-to = "src/main/headers/app.h"')
        rootProject.file("performance.scenarios").text.contains('apply-cpp-change-to = "src/main/cpp/app.cpp"')

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.0.0/testApp-1.0.0.pom").file
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()

        rootProject.src.list().size() == sourceFiles + 1

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

        def rootProject = build.project(":").isCppApplication()

        rootProject.headers.contains("app.h", "app_defs1.h")

        def srcDir = rootProject.src
        srcDir.contains("app.cpp", "app_private.h", "appimplapi.cpp", "appimplcore.cpp")
        rootProject.testHeaders.contains("app_test.h")
        rootProject.testSrc.contains("test_main.cpp", "app_test.cpp", "appimplapi_test.cpp", "appimplcore_test.cpp")

        def lib1 = build.project(":lib").isCppLibrary()
        lib1.publicHeaders.contains("lib.h")
        lib1.headers.contains("lib_impl.h")
        lib1.src.contains("lib.cpp", "lib_private.h", "libimplapi.cpp", "libimplcore.cpp")
        lib1.testHeaders.contains("lib_test.h")
        lib1.testSrc.contains("test_main.cpp", "lib_test.cpp",  "libimplapi_test.cpp", "libimplcore_test.cpp")

        rootProject.dependsOn(lib1)
        lib1.dependsOn()

        rootProject.file("performance.scenarios").text.contains('apply-h-change-to = "lib1api/src/main/public/lib1api.h"')
        rootProject.file("performance.scenarios").text.contains('apply-cpp-change-to = "lib1api/src/main/cpp/lib1api.cpp"')

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.0.0/testApp-1.0.0.pom").file
        file("repo/test/lib1api/1.0.0/lib1api-1.0.0.pom").file
    }

    def "can generate build with API dependencies between projects"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "6")

        then:
        build.isBuild()

        def lib1 = build.project(":lib1api1").isCppLibrary()
        lib1.publicHeaders.file("lib1api1.h").text.contains("#include \"lib2.h\"")
        lib1.publicHeaders.file("lib1api1.h").text.contains("void doSomethingWith(Lib2& p);")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
        build.buildSucceeds("publish")
    }

    def "can generate build with API dependencies between projects and source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "6", "--source-files", "6")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.headers.file("app.h").text.contains("void doSomethingWith(AppImpl2Api1& p);")

        def lib1 = build.project(":lib1api1").isCppLibrary()
        lib1.headers.file("lib1api1_impl.h").text.contains("void doSomethingWith(Lib1Api1Impl2Api1& p);")

        def lib2 = build.project(":lib1api2").isCppLibrary()
        lib2.headers.file("lib1api2_impl.h").text.contains("void doSomethingWith(Lib1Api2Impl2Api1& p);")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
        build.buildSucceeds("publish")
    }

    def "can generate multi-project build with simple macro includes"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--macro-include", "simple")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.headers.file("app.h").text.contains("#include APP_DEFS1_H")
        rootProject.headers.file("app.h").text.contains('#define APP_DEFS1_H "app_defs1.h"')

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate multi-project build with complex macro includes"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--macro-include", "complex")

        then:
        build.isBuild()

        def rootProject= build.project(":").isCppApplication()
        rootProject.headers.file("app.h").text.contains("#include APP_DEFS1_H")
        rootProject.headers.file("app.h").text.contains('#define APP_DEFS1_H __APP_DEFS1_H(app_defs1.h)')

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate multi-project build with no macro includes"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--macro-include", "none")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        !rootProject.headers.file("app.h").text.contains("#include APP_DEFS1_H")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate multi-project build with boost includes"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--boost")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.src.file("app_private.h").text.contains('#include <boost/asio.hpp>')

        def lib1 = build.project(":lib1api").isCppLibrary()
        lib1.src.file("lib1api_private.h").text.contains('#include <boost/asio.hpp>')

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate 3 project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "3")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        def lib1 = build.project(":lib1api").isCppLibrary()
        def lib2 = build.project(":lib2api").isCppLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.0.0/testApp-1.0.0.pom").file
        file("repo/test/lib1api/1.0.0/lib1api-1.0.0.pom").file
        file("repo/test/lib2api/1.0.0/lib2api-1.0.0.pom").file
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.headers.contains("app.h", "app_defs1.h")
        rootProject.src.contains("app.cpp", "app_private.h", "appimpl1api.cpp", "appimpl2api.cpp")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")

        build.buildSucceeds("publish")
        file("repo/test/testApp/1.0.0/testApp-1.0.0.pom").file

        where:
        projects << ["4", "5", "10", "20"]
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.src.list().size() == sourceFiles + 1

        def lib1 = build.project(":lib1api1").isCppLibrary()
        lib1.src.list().size() == sourceFiles + 1

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
        def rootProject = build.project(":").isCppApplication()
        rootProject.headers.contains("app.h", "app_defs1.h")
        rootProject.src.contains("app_private.h", "app_private_defs1.h", "app.cpp", "appimpl1api.cpp", "appimpl2api.cpp")

        def lib1 = build.project(":lib1api").isCppLibrary()
        lib1.publicHeaders.contains("lib1api.h")
        lib1.headers.contains("lib1api_impl.h", "lib1api_impl_defs1.h")
        lib1.src.contains("lib1api_private.h", "lib1api.cpp", "lib1apiimpl1api.cpp", "lib1apiimpl2api.cpp")

        build.buildSucceeds(":installDebug")
        build.app("build/install/main/debug/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate multi-project build with 8 header files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "2", "--header-files", "8")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()
        rootProject.headers.contains("app.h", "app_defs1.h", "app_defs2.h", "app_defs3.h")
        rootProject.src.contains("app_private.h", "app_private_defs1.h", "app_private_defs2.h", "app_private_defs3.h", "app.cpp", "appimpl1api.cpp", "appimpl2api.cpp")

        def lib1 = build.project(":lib1api").isCppLibrary()
        lib1.publicHeaders.contains("lib1api.h", "lib1api_defs1.h")
        lib1.headers.contains("lib1api_impl.h", "lib1api_impl_defs1.h", "lib1api_impl_defs2.h")
        lib1.src.contains("lib1api_private.h", "lib1api_private_defs1.h", "lib1api_private_defs2.h", "lib1api.cpp", "lib1apiimpl1api.cpp", "lib1apiimpl2api.cpp")

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
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--included-builds", "1")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()

        def child = build(file("child1api"))
        child.isBuild()
        child.project(":").isEmptyProject()
        def lib1 = child.project(":child1apilib1api").isCppLibrary()
        def lib2 = child.project(":child1apilib2api").isCppLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can re-generate a build using different values"() {
        when:
        new Main().run("cpp", "--projects", "2", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()
        build.project(":lib1api").isCppLibrary()

        when:
        new Main().run("cpp", "--source-files", "1", "--dir", projectDir.absolutePath)

        then:
        build.project(":").isCppApplication()
        build.withGit { git ->
            def status = git.status().call()
            assert status.clean
            assert !status.hasUncommittedChanges()
            assert status.untracked.empty
            assert status.untrackedFolders.empty
        }
    }

}
