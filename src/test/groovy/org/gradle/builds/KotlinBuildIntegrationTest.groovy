package org.gradle.builds

class KotlinBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate Kotlin application"() {
        when:
        new Main().run("kotlin", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(".").isEmptyProject()
    }

    def "can generate multi-project Kotlin application"() {
        when:
        new Main().run("kotlin", "--dir", projectDir.absolutePath, "--projects", "3")

        then:
        build.isBuild()
        build.project(".").isEmptyProject()
        build.project("libapi").isEmptyProject()
        build.project("libcore").isEmptyProject()
    }
}
