package org.gradle.builds

class JavaBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def "can generate build with source dependencies"() {
        when:
        new Main().run("java", "--dir", projectDir.absolutePath, "--source-dep-builds", "2")

        then:
        build.isBuild()

        build.rootProject.isJavaApplication()
        def srcDir = build.rootProject.file("src/main/java/org/gradle/example/app")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.srcapilibapi.SrcApiLibApi.getSomeValue()")
        new File(srcDir, "AppImplApi.java").text.contains("org.gradle.example.srcapilibapi.SrcApiLibApi.INT_CONST")

        def child1 = build(file("external/sourceApi"))
        child1.isBuild()
        child1.rootProject.isEmptyProject()
        child1.project(":srcapilibapi").isJavaLibrary()
        child1.project(":srcapilibcore").isJavaLibrary()

        def child2 = build(file("external/sourceCore"))
        child2.isBuild()
        child2.rootProject.isEmptyProject()
        child2.project(":srccorelibapi").isJavaLibrary()
        child2.project(":srccorelibcore").isJavaLibrary()

        build.buildSucceeds(":installDist")

        def app = build.app("build/install/testApp/bin/testApp")
        app.isApp()
        app.libDir.list() as Set == [
                "srcapilibapi-1.0.0.jar",
                "srcapilibcore-1.0.0.jar",
                "srccorelibapi-1.0.0.jar",
                "srccorelibcore-1.0.0.jar",
                "slf4j-api-1.7.25.jar",
                "slf4j-simple-1.7.25.jar",
                "testApp.jar"
        ] as Set
        app.succeeds()

        build.buildSucceeds("build")
    }
}
