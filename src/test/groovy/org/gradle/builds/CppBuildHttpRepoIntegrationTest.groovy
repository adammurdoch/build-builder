package org.gradle.builds

class CppBuildHttpRepoIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.8-20180507235951+0000"
    }

    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()

        def buildFile = build.project(":").file("build.gradle")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api1:1.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api2:1.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib2api:1.0.0'")

        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib1Api1")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib1Api2")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib2Api")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isCppLibrary()
        repoBuild.project(':extlib1api2').isCppLibrary()
        repoBuild.project(':extlib2api').isCppLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0-cpp-api-headers.zip").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0-cpp-api-headers.zip").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0-cpp-api-headers.zip").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo with single library"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--http-repo-libraries", "1", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()

        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("Ext ext;")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isCppLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/ext/1.0.0/ext-1.0.0.pom").file
        file("http-repo/org/gradle/example/ext/1.0.0/ext-1.0.0-cpp-api-headers.zip").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo with multiple versions"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--http-repo-versions", "3", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppApplication()

        def buildFile = build.project(":").file("build.gradle")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api1:3.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api2:3.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib2api:3.0.0'")

        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib1Api1")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib1Api2")
        new File(srcDir, "appimpl1api.cpp").text.contains("ExtLib2Api")

        def repoBuildV1 = build(file('external/v1'))
        repoBuildV1.isBuild()
        repoBuildV1.project(':').isEmptyProject()
        repoBuildV1.project(':extlib1api1').isCppLibrary()
        repoBuildV1.project(':extlib1api2').isCppLibrary()
        repoBuildV1.project(':extlib2api').isCppLibrary()

        def repoBuildV2 = build(file('external/v2'))
        repoBuildV2.isBuild()
        repoBuildV2.project(':').isEmptyProject()
        repoBuildV2.project(':extlib1api1').isCppLibrary()
        repoBuildV2.project(':extlib1api2').isCppLibrary()
        repoBuildV2.project(':extlib2api').isCppLibrary()

        def repoBuildV3 = build(file('external/v3'))
        repoBuildV3.isBuild()
        repoBuildV3.project(':').isEmptyProject()
        repoBuildV3.project(':extlib1api1').isCppLibrary()
        repoBuildV3.project(':extlib1api2').isCppLibrary()
        repoBuildV3.project(':extlib2api').isCppLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0-cpp-api-headers.zip").file
        file("http-repo/org/gradle/example/extlib1api1/2.0.0/extlib1api1-2.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/2.0.0/extlib1api1-2.0.0-cpp-api-headers.zip").file
        file("http-repo/org/gradle/example/extlib1api1/3.0.0/extlib1api1-3.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/3.0.0/extlib1api1-3.0.0-cpp-api-headers.zip").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
