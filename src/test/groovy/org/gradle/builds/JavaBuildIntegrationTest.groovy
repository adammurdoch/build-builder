package org.gradle.builds

import spock.lang.Unroll

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.isCleanGitRepo()

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["App.java", "AppImplApi.java", "AppImplCore.java"] as Set
        new File(srcDir, "App.java").text.contains("org.gradle.example.app.AppImplApi.getSomeValue()")
        new File(srcDir, "App.java").text.contains("org.gradle.example.app.AppImplApi.INT_CONST")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.app.AppImplCore.getSomeValue()")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.app.AppImplCore.INT_CONST")
        new File(srcDir, "AppImplApi.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate single project build with #sourceFiles source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.project(":").file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10, 20]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["App.java", "AppImplApi.java", "AppImplCore.java"] as Set
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.lib.Lib.getSomeValue()")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.lib.Lib.INT_CONST")

        build.project(":lib").isJavaLibrary()
        build.project(":lib").file("src/main/java/org/gradle/example/lib").list() as Set == ["Lib.java", "LibImplApi.java", "LibImplCore.java"] as Set

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["lib.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    @Unroll
    def "can generate #projects project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list() as Set == ["App.java", "AppImplApi.java", "AppImplCore.java"] as Set

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        projects << ["3", "4", "10", "20"]
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles as String)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":").file("src/main/java/org/gradle/example/app").list().size() == sourceFiles
        build.project(":").file("src/test/java/org/gradle/example/app").list().size() == sourceFiles

        build.project(":libapi1").isJavaLibrary()
        build.project(":libapi1").file("src/main/java/org/gradle/example/libapi1").list().size() == sourceFiles
        build.project(":libapi1").file("src/test/java/org/gradle/example/libapi1").list().size() == sourceFiles

        build.project(":libapi2").isJavaLibrary()
        build.project(":libcore").isJavaLibrary()

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << [1, 2, 5, 10, 20]
    }

    def "can generate composite build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--included-builds", "1")

        then:
        build.isBuild()

        def rootProject = build.project(":").isJavaApplication()

        def child = build(file("child"))
        child.isBuild()
        child.project(":").isEmptyProject()
        def lib1 = child.project(":childlibapi").isJavaLibrary()
        def lib2 = child.project(":childlibcore").isJavaLibrary()

        rootProject.dependsOn(lib1)
        lib1.dependsOn(lib2)
        lib2.dependsOn()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["childlibapi-1.0.0.jar", "childlibcore-1.0.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can generate composite build with 3 builds"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--included-builds", "2")

        then:
        build.isBuild()

        def rootProject = build.project(":").isJavaApplication()

        def child1 = build(file("childapi"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        def child1lib1 = child1.project(":childapilibapi").isJavaLibrary()
        def child1lib2 = child1.project(":childapilibcore").isJavaLibrary()

        def child2 = build(file("childcore"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        def child2lib1 = child2.project(":childcorelibapi").isJavaLibrary()
        def child2lib2 = child2.project(":childcorelibcore").isJavaLibrary()

        rootProject.dependsOn(child1lib1)
        child1lib1.dependsOn(child1lib2, child2lib1)
        child1lib2.dependsOn(child2lib1)
        child2lib1.dependsOn(child2lib2)
        child2lib2.dependsOn()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["childapilibapi-1.0.0.jar", "childapilibcore-1.0.0.jar", "childcorelibapi-1.0.0.jar", "childcorelibcore-1.0.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
