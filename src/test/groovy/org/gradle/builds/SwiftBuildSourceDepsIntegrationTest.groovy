package org.gradle.builds

class SwiftBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def "can generate build with source dependencies"() {
        when:
        new Main().run("swift", "--dir", projectDir.absolutePath, "--source-dep-builds", "2")

        then:
        build.isBuild()

        def rootProject = build.rootProject.isSwiftApplication()

        def child1 = build(file("external/sourceApi"))
        child1.isBuild()
        child1.rootProject.isEmptyProject()
        def child1lib1 = child1.project(":srcapilibapi").isSwiftLibrary()
        def child1lib2 = child1.project(":srcapilibcore").isSwiftLibrary()

        def child2 = build(file("external/sourceCore"))
        child2.isBuild()
        child2.rootProject.isEmptyProject()
        def child2lib1 = child2.project(":srccorelibapi").isSwiftLibrary()
        def child2lib2 = child2.project(":srccorelibcore").isSwiftLibrary()

        rootProject.dependsOn(child1lib1)
        child1lib1.dependsOn(child1lib2, child2lib1)
        child1lib2.dependsOn(child2lib1)
        child2lib1.dependsOn(child2lib2)
        child2lib2.dependsOn()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
