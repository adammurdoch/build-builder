package org.gradle.builds

class CppBuildComplexCompositionIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.6-20180129223723+0000"
    }

    def "can generate build"() {
        given:
        useIsolatedUserHome()

        when:
        new Main().run("cpp", "--http-repo", "--included-builds", "2", "--source-dep-libraries", "2", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()

        build(file('external/v1')).isBuild()
        build(file('external/source')).isBuild()
        build(file('child1')).isBuild()
        build(file('child2')).isBuild()

        def serverBuild = build(file('repo-server'))
        serverBuild.buildSucceeds("installDist")

        def server = serverBuild.app("build/install/repo/bin/repo").start()
        waitFor(new URI("http://localhost:5005"))

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")

        cleanup:
        server?.kill()
    }

}
