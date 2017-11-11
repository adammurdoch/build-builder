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
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5", "10"]
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
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isAndroidApplication()
        build.project(":lib1api1").isAndroidLibrary()
        build.project(":lib1api2").isAndroidLibrary()
        build.project(":lib2api").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5", "10"]
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
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api1.RepoLib1Api1Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api1.RepoLib1Api1Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api1.R.string.repolib1api1_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api2.RepoLib1Api2Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api2.RepoLib1Api2Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib1api2.R.string.repolib1api2_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib2api.RepoLib2ApiActivity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib2api.RepoLib2ApiActivity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repolib2api.R.string.repolib2api_string")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repolib1api1').isAndroidLibrary()
        repoBuild.project(':repolib1api2').isAndroidLibrary()
        repoBuild.project(':repolib2api').isAndroidLibrary()

        repoBuild.buildSucceeds("installDist")

        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repolib2api/1.2/repolib2api-1.2.aar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repolib2api/1.2/repolib2api-1.2.pom").file

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
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.core1.RepoCore1.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.core1.RepoCore1.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.lib11.RepoLib11Activity.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.lib11.RepoLib11Activity.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.lib11.R.string.repo_lib1_1_string")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.lib12.RepoLib12.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.repo.lib12.RepoLib12.INT_CONST")

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
