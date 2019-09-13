package org.gradle.builds

class KotlinBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate Kotlin application"() {
        when:
        new Main().run("kotlin", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.rootProject.isKotlinApplication()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["kotlin-stdlib-1.3.41.jar", "kotlin-stdlib-jdk8-1.3.41.jar", "kotlin-stdlib-common-1.3.41.jar", "kotlin-stdlib-jdk7-1.3.41.jar", "annotations-13.0.jar", "testApp.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }

    def "can generate 3 project Kotlin application"() {
        when:
        new Main().run("kotlin", "--dir", projectDir.absolutePath, "--projects", "3")

        then:
        build.isBuild()
        def rootProject = build.rootProject.isKotlinApplication()
        def libApi = build.project(":libapi").isKotlinLibrary()
        def libCore = build.project(":libcore").isKotlinLibrary()

        rootProject.dependsOn(libApi)
        libApi.dependsOn(libCore)
        libCore.dependsOn()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == ["kotlin-stdlib-1.3.41.jar", "kotlin-stdlib-jdk8-1.3.41.jar", "kotlin-stdlib-common-1.3.41.jar", "kotlin-stdlib-jdk7-1.3.41.jar", "annotations-13.0.jar", "testApp.jar", "libapi.jar", "libcore.jar"] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
