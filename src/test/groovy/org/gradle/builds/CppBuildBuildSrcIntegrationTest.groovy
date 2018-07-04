package org.gradle.builds

class CppBuildBuildSrcIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.8"
    }

    def "can generate buildsrc"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--buildsrc")

        then:
        build.isBuild()

        def buildSrc = build(file("buildSrc"))
        buildSrc.isBuild()
        buildSrc.project(":").isJavaPlugin()

        build.buildSucceeds(":show")
    }
}
