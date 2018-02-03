package org.gradle.builds

class AndroidBuildHttpRepoIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("android", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.R.string.extlib1api1_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.R.string.extlib1api2_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.R.string.extlib2api_string")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isAndroidLibrary()
        repoBuild.project(':extlib1api2').isAndroidLibrary()
        repoBuild.project(':extlib2api').isAndroidLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.aar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo and Java libraries"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("android", "--projects", "3", "--java", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.R.string.extlib1api1_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2Api.INT_CONST")

        build.project(":lib1api").isAndroidLibrary()
        build.project(":lib2api").isJavaLibrary()

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isAndroidLibrary()
        repoBuild.project(':extlib1api2').isJavaLibrary()
        repoBuild.project(':extlib2api').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.aar").file
        file("http-repo/org/gradle/example/extlib1api1/1.0.0/extlib1api1-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlib1api2/1.0.0/extlib1api2-1.0.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/1.0.0/extlib2api-1.0.0.pom").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
