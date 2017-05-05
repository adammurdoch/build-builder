package org.gradle.builds

class CppBuildIntegrationTest extends AbstractIntegrationTest {
    def "can generate single project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath)

        then:
        build.isBuild()
        build.project(":").isCppProject()
        build.project(":").file("src/main/headers").list() as Set == ["app.h"] as Set
        build.project(":").file("src/main/cpp").list() as Set == ["app.cpp", "app_impl1_1.cpp", "app_nodeps1.cpp"] as Set

        build.buildSucceeds(":installMain")
        build.app("build/install/main/testApp").succeeds()

        build.buildSucceeds("build")
    }

    def "can generate single project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppProject()

        build.buildSucceeds(":installMain")
        build.app("build/install/main/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }

    def "can generate multi-project build"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", projects)

        then:
        build.isBuild()
        build.project(":").isCppProject()
        build.project(":").file("src/main/headers").list() as Set == ["app.h"] as Set
        build.project(":").file("src/main/cpp").list() as Set == ["app.cpp", "app_impl1_1.cpp", "app_nodeps1.cpp"] as Set
        build.project(":core1").isCppProject()
        build.project(":core1").file("src/main/headers").list() as Set == ["core1.h", "core1_impl.h"] as Set
        build.project(":core1").file("src/main/cpp").list() as Set == ["core1.cpp", "core1_impl1_1.cpp", "core1_nodeps1.cpp"] as Set

        build.buildSucceeds(":installMain")
        build.app("build/install/main/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        projects << ["2", "3", "4", "5"]
    }

    def "can generate multi-project build with specified number of source files"() {
        when:
        new Main().run("cpp", "--dir", projectDir.absolutePath, "--projects", "4", "--source-files", sourceFiles)

        then:
        build.isBuild()
        build.project(":").isCppProject()
        build.project(":lib1_1").isCppProject()
        build.project(":lib1_2").isCppProject()
        build.project(":core1").isCppProject()

        build.buildSucceeds(":installMain")
        build.app("build/install/main/testApp").succeeds()

        build.buildSucceeds("build")

        where:
        sourceFiles << ["1", "2", "5"]
    }
}
