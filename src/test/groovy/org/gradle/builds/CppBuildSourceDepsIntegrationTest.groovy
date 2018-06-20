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

        build.project(":").isCppApplication()
        def srcDir = build.project(":").file("src/main/cpp")
        new File(srcDir, "appimpl1api.cpp").text.contains("Src1ApiLib1Api")
        new File(srcDir, "appimpl1api.cpp").text.contains("Src1ApiLib2Api")

        def child1 = build(file("external/source1Api"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":src1apilib1api").isCppLibrary()
        child1.project(":src1apilib2api").isCppLibrary()
        def srcDir1 = child1.project(":src1apilib2api").file("src/main/cpp")
        new File(srcDir1, "src1apilib2apiimpl1api.cpp").text.contains("Src2ApiLib1Api")
        new File(srcDir1, "src1apilib2apiimpl1api.cpp").text.contains("Src2ApiLib1Api")

        def child2 = build(file("external/source2Api"))
        child2.isBuild()
        child2.project(":").isEmptyProject()
        child2.project(":src2apilib1api").isCppLibrary()
        child2.project(":src2apilib1api").isCppLibrary()

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

        build.project(":").isCppApplication()

        def child1 = build(file("external/source1Api1"))
        child1.isBuild()
        child1.project(":").isEmptyProject()
        child1.project(":src1api1lib1api").isCppLibrary()

        def child2 = build(file("external/source1Api2"))
        child2.isBuild()
        child2.project(":src1api2lib1api").isCppLibrary()

        def child3 = build(file("external/source1Core"))
        child3.isBuild()
        child3.project(":src1corelib1api").isCppLibrary()

        def child4 = build(file("external/source2Api"))
        child4.isBuild()
        child4.project(":src2apilib1api").isCppLibrary()

        build.buildSucceeds(":installDebug")

        def app = build.app("build/install/main/debug/testApp")
        app.succeeds()

        build.buildSucceeds("build")
    }

}
