package org.gradle.builds

class CppBuildSourceDepsIntegrationTest extends AbstractIntegrationTest {
    def setup() {
        gradleVersion = "4.8-20180507235951+0000"
    }

    def "can generate build with source dependencies"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-dep-builds", "2")

        then:
        build.isBuild()

        def rootProject = build.project(":").isCppApplication()

        def child1 = build(file("external/source1Api"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        def child1lib1 = child1.project(":src1apilib1api").isCppLibrary()
        def child1lib2 = child1.project(":src1apilib2api").isCppLibrary()

        def child2 = build(file("external/source2Api"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        def child2lib1 = child2.project(":src2apilib1api").isCppLibrary()
        def child2lib2 = child2.project(":src2apilib2api").isCppLibrary()

        rootProject.dependsOn(child1lib1, child1lib2)
        child1lib1.dependsOn(child1lib2, child2lib1, child2lib2)
        child1lib2.dependsOn(child2lib1, child2lib2)
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

        def rootProject = build.project(":").isCppApplication()

        def child1 = build(file("external/source1Api1"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        def child1lib1 = child1.project(":src1api1lib1api").isCppLibrary()
        def child1lib2 = child1.project(":src1api1lib2api").isCppLibrary()

        def child2 = build(file("external/source1Api2"))
        child2.isBuild()
        def child2lib1 = child2.project(":src1api2lib1api").isCppLibrary()
        def child2lib2 = child2.project(":src1api2lib2api").isCppLibrary()

        def child3 = build(file("external/source1Core"))
        child3.isBuild()
        def child3lib1 = child3.project(":src1corelib1api").isCppLibrary()
        def child3lib2 = child3.project(":src1corelib2api").isCppLibrary()

        def child4 = build(file("external/source2Api"))
        child4.isBuild()
        def child4lib1 = child4.project(":src2apilib1api").isCppLibrary()
        def child4lib2 = child4.project(":src2apilib2api").isCppLibrary()

        rootProject.dependsOn(child1lib1, child1lib2, child2lib1, child2lib2)
        child1lib1.dependsOn(child1lib2, child3lib1, child3lib2, child4lib1, child4lib2)
        child2lib1.dependsOn(child2lib2, child3lib1, child3lib2, child4lib1, child4lib2)
        child3lib1.dependsOn(child3lib2)
        child4lib1.dependsOn(child4lib2)

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
