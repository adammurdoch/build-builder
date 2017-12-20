package org.gradle.builds

import spock.lang.Unroll

class AndroidBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.app.AppImpl1Api.getSomeValue()")
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.app.AppImpl1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.AppImpl2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.AppImpl2Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")
        new File(srcDir, "AppImpl1Api.java").text.contains("android.support.v4.app.NavUtils.PARENT_ACTIVITY")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.R.string.testapp_string")

        // TODO check tests

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.project(":").file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.lib1api.Lib1ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.lib1api.Lib1ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.lib1api.R.string.lib1api_string")

        build.project(":lib1api").isAndroidLibrary()
        build.project(":lib1api").file("src/main/java/org/gradle/example/lib1api").list() as Set == ["Lib1ApiActivity.java", "Lib1ApiImpl1Api.java", "Lib1ApiImpl2Api.java"] as Set

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list() as Set == ["AppMainActivity.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        projects << ["3", "5", "10"]
    }

    def "can generate 2 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2", "--java")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1api").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    def "can generate 3 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "3", "--java")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1api").isAndroidLibrary()
        build.project(":lib2api").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    def "can generate 6 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "6", "--java")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1api1").isAndroidLibrary()
        build.project(":lib1api2").isAndroidLibrary()
        build.project(":lib1core").isJavaProject()
        build.project(":lib2api1").isAndroidLibrary()
        build.project(":lib2api2").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.project(":").file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.project(":lib1api1").isAndroidLibrary()
        build.project(":lib1api1").file("src/main/java/org/gradle/example/lib1api1").list().size() == sourceFiles
        build.project(":lib1api1").file("src/test/java/org/gradle/example/lib1api1").list().size() == sourceFiles

        build.project(":lib1api2").isAndroidLibrary()
        build.project(":lib2api").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10]
    }

    def "can generate composite build"() {
        when:
        gradleVersion = "4.2"
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2", "--version", "3.0.0-beta1")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1api").isAndroidLibrary()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.R.string.child1lib1api_string")

        def coreSrcDir = build.project(":lib1api").file("src/main/java/org/gradle/example/lib1api")
        new File(coreSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1ApiActivity.INT_CONST")
        new File(coreSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1ApiActivity.INT_CONST")
        new File(coreSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.child1lib1api.R.string.child1lib1api_string")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1lib1api").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

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

        file("http-repo/org/gradle/example/extlib1api1/1.0/extlib1api1-1.0.pom").file
        file("http-repo/org/gradle/example/extlib1api1/1.0/extlib1api1-1.0.aar").file

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

        file("http-repo/org/gradle/example/extlib1api1/1.0/extlib1api1-1.0.aar").file
        file("http-repo/org/gradle/example/extlib1api1/1.0/extlib1api1-1.0.pom").file
        file("http-repo/org/gradle/example/extlib1api2/1.0/extlib1api2-1.0.jar").file
        file("http-repo/org/gradle/example/extlib1api2/1.0/extlib1api2-1.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.pom").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
