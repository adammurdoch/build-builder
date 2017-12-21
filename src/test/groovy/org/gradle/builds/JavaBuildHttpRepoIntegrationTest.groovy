package org.gradle.builds

class JavaBuildHttpRepoIntegrationTest extends AbstractIntegrationTest {
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
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.Ext.getSomeValue()")
        new File(srcDir, "AppImpl1Api.java").text.contains("org.gradle.example.app.Ext.INT_CONST")

        def repoBuild = build(file('external/v1'))
        repoBuild.isBuild()
        repoBuild.project(':').isJavaLibrary()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        file("http-repo/org/gradle/example/ext/1.0/ext-1.0.pom").file
        file("http-repo/org/gradle/example/ext/1.0/ext-1.0.jar").file

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["ext-1.0.jar", "slf4j-api-1.7.25.jar", "testApp.jar"] as Set
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
