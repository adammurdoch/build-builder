package org.gradle.builds

class CppBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def "can generate build with source dependencies"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-dep-builds", "2")

        then:
        build.isBuild()

        def rootProject = build.rootProject.isCppApplication()

        def child1 = build(file("external/sourceApi"))
        child1.isBuild()
        child1.rootProject.isEmptyProject()
        def child1lib1 = child1.project(":srcapilibapi").isCppLibrary()
        def child1lib2 = child1.project(":srcapilibcore").isCppLibrary()

        def child2 = build(file("external/sourceCore"))
        child2.isBuild()
        child2.rootProject.isEmptyProject()
        def child2lib1 = child2.project(":srccorelibapi").isCppLibrary()
        def child2lib2 = child2.project(":srccorelibcore").isCppLibrary()

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

    def "can generate build with 4 source dependencies"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-dep-builds", "4")

        then:
        build.isBuild()

        def rootProject = build.rootProject.isCppApplication()

        def child1 = build(file("external/sourceApi1"))
        child1.isBuild()
        child1.rootProject.isEmptyProject()
        def child1lib1 = child1.project(":srcapi1libapi").isCppLibrary()
        def child1lib2 = child1.project(":srcapi1libcore").isCppLibrary()

        def child2 = build(file("external/sourceApi2"))
        child2.isBuild()
        def child2lib1 = child2.project(":srcapi2libapi").isCppLibrary()
        def child2lib2 = child2.project(":srcapi2libcore").isCppLibrary()

        def child3 = build(file("external/sourceImpl"))
        child3.isBuild()
        def child3lib1 = child3.project(":srcimpllibapi").isCppLibrary()
        def child3lib2 = child3.project(":srcimpllibcore").isCppLibrary()

        def child4 = build(file("external/sourceCore"))
        child4.isBuild()
        def child4lib1 = child4.project(":srccorelibapi").isCppLibrary()
        def child4lib2 = child4.project(":srccorelibcore").isCppLibrary()

        rootProject.dependsOn(child1lib1, child2lib1)
        child1lib1.dependsOn(child1lib2, child3lib1)
        child2lib1.dependsOn(child2lib2, child3lib1)
        child3lib1.dependsOn(child3lib2, child4lib1)
        child4lib1.dependsOn(child4lib2)

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
