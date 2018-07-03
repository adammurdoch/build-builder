package org.gradle.builds

class AndroidBuildHttpRepoIntegrationTest extends AbstractAndroidIntegrationTest {
    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("android", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isAndroidApplication()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def lib1 = repoBuild.project(':extlibapi1').isAndroidLibrary()
        def lib2 = repoBuild.project(':extlibapi2').isAndroidLibrary()
        def lib3 = repoBuild.project(':extlibcore').isAndroidLibrary()

        rootProject.dependsOn(lib1, lib2)
        lib1.dependsOn(lib3)
        lib2.dependsOn(lib3)
        lib3.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/extlibapi1/1.0.0/extlibapi1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlibapi1/1.0.0/extlibapi1-1.0.0.aar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate multi project build with http repo and Java libraries"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("android", "--projects", "3", "--java", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        def rootProject = build.project(":").isAndroidApplication()

        def lib1 = build.project(":libapi").isAndroidLibrary()
        def lib2 = build.project(":libcore").isJavaLibrary()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        def extlib1 = repoBuild.project(':extlibapi1').isAndroidLibrary()
        def extlib2 = repoBuild.project(':extlibapi2').isJavaLibrary()
        def extlib3 = repoBuild.project(':extlibcore').isJavaLibrary()

        rootProject.dependsOn(lib1, extlib1, extlib2)
        lib1.dependsOn(lib2, extlib1, extlib2)
        lib2.dependsOn(extlib2)
        extlib1.dependsOn(extlib3)
        extlib2.dependsOn(extlib3)
        extlib3.dependsOn()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/extlibapi1/1.0.0/extlibapi1-1.0.0.aar").file
        file("http-repo/org/gradle/example/extlibapi1/1.0.0/extlibapi1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlibapi2/1.0.0/extlibapi2-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlibapi2/1.0.0/extlibapi2-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlibcore/1.0.0/extlibcore-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlibcore/1.0.0/extlibcore-1.0.0.pom").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
