package org.gradle.builds

class JavaBuildBuildSrcIntegrationTest extends AbstractIntegrationTest {
    def "can generate buildsrc"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--buildsrc")

        then:
        build.isBuild()

        def buildSrc = build(file("buildSrc"))
        buildSrc.isBuild()

        def lib = buildSrc.project(":").isJavaPlugin()
        lib.src.contains("org/gradle/example/buildsrc/ShowPlugin.java")

        build.buildSucceeds(":show")
    }
}
