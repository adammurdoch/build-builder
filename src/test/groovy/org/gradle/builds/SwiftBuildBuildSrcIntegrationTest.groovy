package org.gradle.builds

class SwiftBuildBuildSrcIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.5"
    }

    def "can generate buildsrc"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--buildsrc")

        then:
        build.isBuild()

        def buildSrc = build(file("buildSrc"))
        buildSrc.isBuild()
        buildSrc.project(":").isJavaPlugin()

        build.buildSucceeds(":show")
    }
}
