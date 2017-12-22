package org.gradle.builds

import spock.lang.Unroll

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        srcDir.list() as Set == ["App.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set
        new File(srcDir, "App.java").text.contains("org.gradle.example.app.AppImpl1Api.getSomeValue()")
        new File(srcDir, "App.java").text.contains("org.gradle.example.app.AppImpl1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.AppImpl2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.AppImpl2Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")

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
        srcDir.list() as Set == ["App.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.lib1api.Lib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.lib1api.Lib1Api.INT_CONST")

        build.project(":lib1api").isJavaLibrary()
        build.project(":lib1api").file("src/main/java/org/gradle/example/lib1api").list() as Set == ["Lib1Api.java", "Lib1ApiImpl1Api.java", "Lib1ApiImpl2Api.java"] as Set

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["lib1api.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
        build.project(":").file("src/main/java/org/gradle/example/app").list() as Set == ["App.java", "AppImpl1Api.java", "AppImpl2Api.java"] as Set

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

        build.project(":lib1api1").isJavaLibrary()
        build.project(":lib1api1").file("src/main/java/org/gradle/example/lib1api1").list().size() == sourceFiles
        build.project(":lib1api1").file("src/test/java/org/gradle/example/lib1api1").list().size() == sourceFiles

        build.project(":lib1api2").isJavaLibrary()
        build.project(":lib2api").isJavaLibrary()

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

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib2api.Child1Lib2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib2api.Child1Lib2Api.INT_CONST")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1lib1api").isJavaLibrary()
        child.project(":child1lib2api").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["child1lib1api-1.0.jar", "child1lib2api-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can generate composite build with 3 builds"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--included-builds", "2")

        then:
        build.isBuild()

        build.project(":").isJavaApplication()

        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib2api.Child1Lib2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib2api.Child1Lib2Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child2lib1api.Child2Lib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child2lib1api.Child2Lib1Api.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child2lib2api.Child2Lib2Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child2lib2api.Child2Lib2Api.INT_CONST")

        def child1 = build(file("child1"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":child1lib1api").isJavaLibrary()
        child1.project(":child1lib2api").isJavaLibrary()

        def child2 = build(file("child2"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        child2.project(":child2lib1api").isJavaLibrary()
        child2.project(":child2lib2api").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["child1lib1api-1.0.jar", "child1lib2api-1.0.jar", "child2lib1api-1.0.jar", "child2lib2api-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
