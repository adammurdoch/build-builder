package org.gradle.builds

class SwiftBuildBuildSrcIntegrationTest extends AbstractIntegrationTest {
    def "can generate buildsrc"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--buildsrc")

        then:
        build.isBuild()

        def buildSrc = build(file("buildSrc"))
        buildSrc.isBuild()
        buildSrc.rootProject.isJavaPlugin()

        build.buildSucceeds(":show")
    }
}
