package org.gradle.builds

import spock.lang.Unroll

class AndroidBuildIntegrationTest extends AbstractAndroidIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        def srcDir = build.rootProject.file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImplApi.java", "AppImplCore.java"] as Set
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.app.AppImplApi.getSomeValue()")
        new File(srcDir, "AppMainActivity.java").text.contains("org.gradle.example.app.AppImplApi.INT_CONST")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.app.AppImplCore.getSomeValue()")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.app.AppImplCore.INT_CONST")
        new File(srcDir, "AppImplApi.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")
        new File(srcDir, "AppImplApi.java").text.contains("android.support.v4.app.NavUtils.PARENT_ACTIVITY")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.app.R.string.testapp_string")

        // TODO check tests

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.rootProject.file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.rootProject.file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        def srcDir = build.rootProject.file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["AppMainActivity.java", "AppImplApi.java", "AppImplCore.java"] as Set
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.lib.LibActivity.getSomeValue()")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.lib.LibActivity.INT_CONST")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.lib.R.string.lib_string")

        build.project(":lib").isAndroidLibrary()
        build.project(":lib").file("src/main/java/org/gradle/example/lib").list() as Set == ["LibActivity.java", "LibImplApi.java", "LibImplCore.java"] as Set

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.rootProject.file("src/main/java/org/gradle/example/app").list() as Set == ["AppMainActivity.java", "AppImplApi.java", "AppImplCore.java"] as Set

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        projects << ["3", "5", "10"]
    }

    def "can generate 2 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "2", "--java")

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.project(":lib").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    def "can generate 3 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "3", "--java")

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.project(":libapi").isAndroidLibrary()
        build.project(":libcore").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    def "can generate 6 project build containing some java libraries"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "6", "--java")

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.project(":lib1api1").isAndroidLibrary()
        build.project(":lib1api2").isAndroidLibrary()
        build.project(":lib1impl").isAndroidLibrary()
        build.project(":lib1core").isJavaLibrary()
        build.project(":lib2").isJavaLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.rootProject.isAndroidApplication()
        build.rootProject.file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.rootProject.file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.project(":libapi1").isAndroidLibrary()
        build.project(":libapi1").file("src/main/java/org/gradle/example/libapi1").list().size() == sourceFiles
        build.project(":libapi1").file("src/test/java/org/gradle/example/libapi1").list().size() == sourceFiles

        build.project(":libapi2").isAndroidLibrary()
        build.project(":libcore").isAndroidLibrary()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10]
    }

    def "can generate composite build"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--included-builds", "1")

        then:
        build.isBuild()

        def rootProject = build.rootProject.isAndroidApplication()

        def child = build(file("child"))
        child.isBuild()
        child.rootProject.isEmptyProject()
        def lib1 = child.project(":childlibapi").isAndroidLibrary()
        def lib2 = child.project(":childlibcore").isAndroidLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }
}
