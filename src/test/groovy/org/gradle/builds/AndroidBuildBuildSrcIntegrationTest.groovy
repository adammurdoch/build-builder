package org.gradle.builds

class AndroidBuildBuildSrcIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.1"
    }

    def "can generate buildsrc"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--buildsrc")

        then:
        build.isBuild()

        def buildSrc = build(file("buildSrc"))
        buildSrc.isBuild()
        buildSrc.project(":").isJavaPlugin()

        build.buildSucceeds(":show")
    }
}
