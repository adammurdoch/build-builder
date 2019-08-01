package org.gradle.builds

class AndroidBuildSourceDepsIntegrationTest extends AbstractAndroidIntegrationTest {
    def "can generate build with source dependencies"() {
        when:
        new Main().run("android", "--dir", projectDir.absolutePath, "--source-dep-builds", "2")

        then:
        build.isBuild()

        def rootProject = build.rootProject.isAndroidApplication()

        def child1 = build(file("external/sourceapi"))
        child1.isBuild()
        child1.rootProject.isEmptyProject()
        def child1lib1 = child1.project(":srcapilibapi").isAndroidLibrary()
        def child1lib2 = child1.project(":srcapilibcore").isAndroidLibrary()

        def child2 = build(file("external/sourcecore"))
        child2.isBuild()
        child2.rootProject.isEmptyProject()
        def child2lib1 = child2.project(":srccorelibapi").isAndroidLibrary()
        def child2lib2 = child2.project(":srccorelibcore").isAndroidLibrary()

        rootProject.dependsOn(child1lib1)
        child1lib1.dependsOn(child1lib2, child2lib1)
        child1lib2.dependsOn(child2lib1)
        child2lib1.dependsOn(child2lib2)
        child2lib2.dependsOn()

        build.buildSucceeds(":assembleDebug")
        file("build/outputs/apk/debug/testApp-debug.apk").exists()

        build.buildSucceeds("build")
    }

}
