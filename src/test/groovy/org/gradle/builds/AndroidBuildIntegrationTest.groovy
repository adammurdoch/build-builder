package org.gradle.builds

import spock.lang.Unroll

class AndroidBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.AppImpl1_1.getSomeValue()")
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.AppImpl1_1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.AppNoDeps1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.AppNoDeps1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")
        new File(srcDir, "AppImpl1_1.java").text.contains("android.support.v4.app.NavUtils.PARENT_ACTIVITY")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.R.string.testapp_string")

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.core1.Core1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.core1.Core1Activity.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.core1.R.string.core1_string")

        build.project(":core1").isAndroidLibrary()
        build.project(":core1").file("src/main/java/org/gradle/example/core1").list() as Set == ["Core1Activity.java", "Core1Impl1_1.java", "Core1NoDeps1.java"] as Set

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
        build.project(":").file("src/main/java/org/gradle/example").list() as Set == ["AppMainActivity.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set

        build.project(":lib1_1").isAndroidLibrary()
        build.project(":lib1_1").file("src/main/java/org/gradle/example/lib1_1").list() as Set == ["Lib1_1Activity.java", "Lib1_1Impl1_1.java", "Lib1_1NoDeps1.java"] as Set

        build.project(":core1").isAndroidLibrary()
        build.project(":core1").file("src/main/java/org/gradle/example/core1").list() as Set == ["Core1Activity.java", "Core1Impl1_1.java", "Core1NoDeps1.java"] as Set

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        projects << ["3", "5"]
    }

    def "can generate 3 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "3", "--java")

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1_1").isAndroidLibrary()
        build.project(":core1").isJavaLibrary()

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
        build.project(":lib1_1").isAndroidLibrary()
        build.project(":lib1_2").isAndroidLibrary()
        build.project(":lib1_3").isJavaProject()
        build.project(":core1").isAndroidLibrary()
        build.project(":core2").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":core1").isAndroidLibrary()
        build.project(":lib1_1").isAndroidLibrary()
        build.project(":lib1_2").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("android", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.Repo_core1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.Repo_core1Activity.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.R.string.repo_core1_string")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.Repo_lib1_1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.Repo_lib1_1Activity.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.R.string.repo_lib1_1_string")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.Repo_lib1_2Activity.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.Repo_lib1_2Activity.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.R.string.repo_lib1_2_string")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repo_lib1_1').isAndroidLibrary()
        repoBuild.project(':repo_lib1_2').isAndroidLibrary()
        repoBuild.project(':repo_core1').isAndroidLibrary()

        repoBuild.buildSucceeds("installDist")

        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.aar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.pom").file

        def server = repoBuild.app("build/install/repo/bin/repo").start()
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
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.Repo_core1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.Repo_core1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.Repo_lib1_1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.Repo_lib1_1Activity.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.R.string.repo_lib1_1_string")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.Repo_lib1_2.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.Repo_lib1_2.INT_CONST")

        build.project(":lib1_1").isAndroidLibrary()
        build.project(":core1").isJavaLibrary()

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repo_lib1_1').isAndroidLibrary()
        repoBuild.project(':repo_lib1_2').isJavaLibrary()
        repoBuild.project(':repo_core1').isJavaLibrary()

        repoBuild.buildSucceeds("installDist")

        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.jar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.pom").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_lib1_1/1.2/repo_lib1_1-1.2.aar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_lib1_1/1.2/repo_lib1_1-1.2.pom").file

        def server = repoBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
