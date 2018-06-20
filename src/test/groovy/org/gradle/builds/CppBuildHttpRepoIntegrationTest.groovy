package org.gradle.builds

class CppBuildHttpRepoIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.8-20180514143239+0000"
    }

    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()

        def buildFile = rootProject.file("build.gradle")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api1:1.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api2:1.0.0'")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def lib1 = repoBuild.project(':extlib1api1').isCppLibrary()
        def lib2 = repoBuild.project(':extlib1api2').isCppLibrary()
        def lib3 = repoBuild.project(':extlib2api').isCppLibrary()

        rootProject.dependsOn(lib1, lib2)
        lib1.dependsOn(lib3)
        lib2.dependsOn(lib3)
        lib3.dependsOn()

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

        def rootProject = build.project(":").isCppApplication()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        def lib1 = repoBuild.project(':').isCppLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn()

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

        def rootProject = build.project(":").isCppApplication()

        def buildFile = rootProject.file("build.gradle")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api1:3.0.0'")
        buildFile.text.contains("implementation 'org.gradle.example:extlib1api2:3.0.0'")

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
        def lib1 = repoBuildV3.project(':extlib1api1').isCppLibrary()
        def lib2 = repoBuildV3.project(':extlib1api2').isCppLibrary()
        def lib3 = repoBuildV3.project(':extlib2api').isCppLibrary()

        rootProject.dependsOn(lib1, lib2)
        lib1.dependsOn(lib3)
        lib2.dependsOn(lib3)
        lib3.dependsOn()

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
