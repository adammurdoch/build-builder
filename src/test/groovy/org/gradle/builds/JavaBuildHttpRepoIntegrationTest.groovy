package org.gradle.builds

class JavaBuildHttpRepoIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("java", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isJavaApplication()

        def buildFile = rootProject.file("build.gradle")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api1:1.0.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api2:1.0.0'")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def lib1 = repoBuild.project(':extlib1api1').isJavaLibrary()
        def lib2 = repoBuild.project(':extlib1api2').isJavaLibrary()
        def lib3 = repoBuild.project(':extlib2api').isJavaLibrary()

        rootProject.dependsOn(lib1, lib2)
        lib1.dependsOn(lib3)
        lib2.dependsOn(lib3)
        lib3.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-1.0.0.jar", "extlib1api2-1.0.0.jar", "extlib2api-1.0.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo with single library"() {
        given:
        useIsolatedUserHome()
        gradleVersion = '4.1'

        when:
        new Main().run("java", "--http-repo", "--http-repo-libraries", "1", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isJavaApplication()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        def lib = repoBuild.project(':').isJavaLibrary()

        rootProject.dependsOn(lib)
        lib.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/ext/1.0.0/ext-1.0.0.pom").file
        file("http-repo/org/gradle/example/ext/1.0.0/ext-1.0.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["ext-1.0.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate multi-project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("java", "--http-repo", "--projects", "3", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        def rootProject = build.project(":").isJavaApplication()
        def lib1 = build.project(":lib1api").isJavaLibrary()
        def lib2 = build.project(":lib2api").isJavaLibrary()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def extlib1 = repoBuild.project(':extlib1api1').isJavaLibrary()
        def extlib2 = repoBuild.project(':extlib1api2').isJavaLibrary()
        def extlib3 = repoBuild.project(':extlib2api').isJavaLibrary()

        rootProject.dependsOn(lib1, extlib1, extlib2)
        lib1.dependsOn(lib2, extlib1, extlib2)
        lib2.dependsOn(extlib1, extlib2)
        extlib1.dependsOn(extlib3)
        extlib2.dependsOn(extlib3)
        extlib3.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-1.0.0.jar", "extlib1api2-1.0.0.jar", "extlib2api-1.0.0.jar", "lib1api.jar", "lib2api.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo with multiple versions"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("java", "--http-repo", "--http-repo-versions", "3", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isJavaApplication()

        def buildFile = rootProject.file("build.gradle")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api1:3.0.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api2:3.0.0'")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def lib1 = repoBuild.project(':extlib1api1').isJavaLibrary()
        def lib2 = repoBuild.project(':extlib1api2').isJavaLibrary()
        def lib3 = repoBuild.project(':extlib2api').isJavaLibrary()

        rootProject.dependsOn(lib1, lib2)
        lib1.dependsOn(lib3)
        lib2.dependsOn(lib3)
        lib3.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/2.0.0/extlib2api-2.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/2.0.0/extlib2api-2.0.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/3.0.0/extlib2api-3.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/3.0.0/extlib2api-3.0.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-3.0.0.jar", "extlib1api2-3.0.0.jar", "extlib2api-3.0.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
