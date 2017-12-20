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
        new Main().run("java", "--dir", projectDir.absolutePath, "--projects", "2", "--builds", "2")

        then:
        build.isBuild()

        build.project(":").isJavaApplication()
        build.project(":lib1api").isJavaLibrary()
        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.INT_CONST")

        def coreSrcDir = build.project(":lib1api").file("src/main/java/org/gradle/example/lib1api")
        new File(coreSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.getSomeValue()")
        new File(coreSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.child1lib1api.Child1Lib1Api.INT_CONST")

        def child = build(file("child1"))
        child.isBuild()
        child.project(":").isEmptyProject()
        child.project(":child1lib1api").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["child1lib1api-1.0.jar", "lib1api.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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

        def buildFile = build.project(":").file("build.gradle")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api1:1.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api2:1.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib2api:1.0'")

        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2Api.getSomeValue()")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isJavaLibrary()
        repoBuild.project(':extlib1api2').isJavaLibrary()
        repoBuild.project(':extlib2api').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.jar").file
        file("http-repo/org/gradle/example/extlib1api2/1.0/extlib1api2-1.0.pom").file
        file("http-repo/org/gradle/example/extlib1api2/1.0/extlib1api2-1.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-1.0.jar", "extlib1api2-1.0.jar", "extlib2api-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

    def "can generate single project build with http repo with single library"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("java", "--http-repo", "--http-repo-libraries", "1", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isJavaApplication()

        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api.ExtLib1Api.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api.ExtLib1Api.INT_CONST")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/extlib1api/1.0/extlib1api-1.0.pom").file
        file("http-repo/org/gradle/example/extlib1api/1.0/extlib1api-1.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
        build.project(":lib1api").isJavaLibrary()
        build.project(":lib2api").isJavaLibrary()

        def srcDir = build.project(":").file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.INT_CONST")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib1api2.ExtLib1Api2.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.extlib2api.ExtLib2Api.getSomeValue()")
        def libSrcDir = build.project(":lib1api").file("src/main/java/org/gradle/example/lib1api")
        new File(libSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.getSomeValue()")
        new File(libSrcDir, "Lib1ApiImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.INT_CONST")
        def coreSrcDir = build.project(":lib2api").file("src/main/java/org/gradle/example/lib2api")
        new File(coreSrcDir, "Lib2ApiImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.getSomeValue()")
        new File(coreSrcDir, "Lib2ApiImpl1Api.java").text.contains("org.gradle.example.extlib1api1.ExtLib1Api1.INT_CONST")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isJavaLibrary()
        repoBuild.project(':extlib1api2').isJavaLibrary()
        repoBuild.project(':extlib2api').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-1.0.jar", "extlib1api2-1.0.jar", "extlib2api-1.0.jar", "lib1api.jar", "lib2api.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
        build.project(":").isJavaApplication()

        def buildFile = build.project(":").file("build.gradle")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api1:3.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib1api2:3.0'")
        buildFile.text.contains("compile 'org.gradle.example:extlib2api:3.0'")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isEmptyProject()
        repoBuild.project(':extlib1api1').isJavaLibrary()
        repoBuild.project(':extlib1api2').isJavaLibrary()
        repoBuild.project(':extlib2api').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/1.0/extlib2api-1.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/2.0/extlib2api-2.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/2.0/extlib2api-2.0.jar").file
        file("http-repo/org/gradle/example/extlib2api/3.0/extlib2api-3.0.pom").file
        file("http-repo/org/gradle/example/extlib2api/3.0/extlib2api-3.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["extlib1api1-3.0.jar", "extlib1api2-3.0.jar", "extlib2api-3.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
