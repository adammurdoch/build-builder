package org.gradle.builds

import spock.lang.Unroll

class JavaBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        srcDir.list() as Set == ["App.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set
        new File(srcDir, "App.java").text.contains("org.gradle.example.AppImpl1_1.getSomeValue()")
        new File(srcDir, "App.java").text.contains("org.gradle.example.AppImpl1_1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.AppNoDeps1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.AppNoDeps1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.slf4j.LoggerFactory.getLogger(\"abc\")")

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
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate 2 project build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "2")

        then:
        build.isBuild()
        build.project(":").isJavaApplication()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        srcDir.list() as Set == ["App.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.core1.Core1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.core1.Core1.INT_CONST")

        build.project(":core1").isJavaLibrary()
        build.project(":core1").file("src/main/java/org/gradle/example/core1").list() as Set == ["Core1.java", "Core1Impl1_1.java", "Core1NoDeps1.java"] as Set

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["core1.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
        build.project(":").file("src/main/java/org/gradle/example").list() as Set == ["App.java", "AppImpl1_1.java", "AppNoDeps1.java"] as Set

        build.project(":lib1_1").isJavaLibrary()
        build.project(":lib1_1").file("src/main/java/org/gradle/example/lib1_1").list() as Set == ["Lib11.java", "Lib11Impl1_1.java", "Lib11NoDeps1.java"] as Set

        build.project(":core1").isJavaLibrary()
        build.project(":core1").file("src/main/java/org/gradle/example/core1").list() as Set == ["Core1.java", "Core1Impl1_1.java", "Core1NoDeps1.java"] as Set

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        projects << ["3", "4", "10"]
    }

    @Unroll
    def "can generate multi-project build with #sourceFiles source files"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":lib1_1").isJavaLibrary()
        build.project(":lib1_2").isJavaLibrary()
        build.project(":core1").isJavaLibrary()

        build.buildSucceeds(":installDist")
        build.app("build/install/testApp/bin/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate composite build"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2")

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":core1").isJavaLibrary()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.child1_core1.Child1Core1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.child1_core1.Child1Core1.INT_CONST")

        def coreSrcDir = build.project(":core1").file("src/main/java/org/gradle/example/core1")
        new File(coreSrcDir, "Core1Impl1_1.java").text.contains("org.gradle.example.child1_core1.Child1Core1.getSomeValue()")
        new File(coreSrcDir, "Core1Impl1_1.java").text.contains("org.gradle.example.child1_core1.Child1Core1.INT_CONST")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1_core1").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["child1_core1-1.2.jar", "core1.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can generate single project build with http repo"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("java", "--http-repo", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()

        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.RepoLib11.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_1.RepoLib11.INT_CONST")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.RepoLib12.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_lib1_2.RepoLib12.INT_CONST")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repo_lib1_1').isJavaLibrary()
        repoBuild.project(':repo_lib1_2').isJavaLibrary()
        repoBuild.project(':repo_core1').isJavaLibrary()

        repoBuild.buildSucceeds("installDist")
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.jar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.pom").file

        def server = repoBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["repo_core1-1.2.jar", "repo_lib1_1-1.2.jar", "repo_lib1_2-1.2.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
        build.project(":").isJavaApplication()
        build.project(":lib1_1").isJavaLibrary()
        build.project(":core1").isJavaLibrary()

        def srcDir = build.project(":").file("src/main/java/org/gradle/example")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.getSomeValue()")
        new File(srcDir, "AppImpl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.INT_CONST")
        def libSrcDir = build.project(":lib1_1").file("src/main/java/org/gradle/example/lib1_1")
        new File(libSrcDir, "Lib11Impl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.getSomeValue()")
        new File(libSrcDir, "Lib11Impl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.INT_CONST")
        def coreSrcDir = build.project(":core1").file("src/main/java/org/gradle/example/core1")
        new File(coreSrcDir, "Core1Impl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.getSomeValue()")
        new File(coreSrcDir, "Core1Impl1_1.java").text.contains("org.gradle.example.repo_core1.RepoCore1.INT_CONST")

        def repoBuild = build(file('repo'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':repo_lib1_1').isJavaLibrary()
        repoBuild.project(':repo_lib1_2').isJavaLibrary()
        repoBuild.project(':repo_core1').isJavaLibrary()

        repoBuild.buildSucceeds("installDist")
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.jar").file
        new File(repoBuild.rootDir, "build/repo/org/gradle/example/repo_core1/1.2/repo_core1-1.2.pom").file

        def server = repoBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["repo_core1-1.2.jar", "repo_lib1_1-1.2.jar", "repo_lib1_2-1.2.jar", "core1.jar", "lib1_1.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }
}
