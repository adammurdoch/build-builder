package org.gradle.builds.assemblers

import org.gradle.builds.model.Build
import spock.lang.Specification

import java.nio.file.Paths

class StructureAssemblerTest extends Specification {
    def assembler = new StructureAssembler(Stub(ProjectDecorator))
    def build = new Build(Paths.get("dir"))

    def "builds dependency graph with one project"() {
        when:
        assembler.arrangeProjects(projects(1), build)

        then:
        build.projects.size() == 1
        build.subprojects.empty
        build.rootProject.dependencies.empty
    }

    def "builds dependency graph with two projects"() {
        when:
        assembler.arrangeProjects(projects(2), build)

        then:
        build.projects.size() == 2
        build.subprojects.size() == 1

        def subprojects = build.subprojects as List
        build.rootProject.dependencies as List == subprojects
    }

    def "builds dependency graph with three projects"() {
        when:
        assembler.arrangeProjects(projects(3), build)

        then:
        build.projects.size() == 3
        build.subprojects.size() == 2

        def subprojects = build.subprojects as List
        build.rootProject.dependencies as List == [subprojects[1]]
        subprojects[1].dependencies as List == [subprojects[0]]
        subprojects[0].dependencies.empty
    }

    def "builds dependency graph with four projects"() {
        when:
        assembler.arrangeProjects(projects(4), build)

        then:
        build.projects.size() == 4
        build.subprojects.size() == 3

        def subprojects = build.subprojects as List
        build.rootProject.dependencies as List == [subprojects[1], subprojects[2]]
        subprojects[1].dependencies as List == [subprojects[0]]
        subprojects[2].dependencies as List == [subprojects[0]]
        subprojects[0].dependencies.empty
    }

    def "builds dependency graph with five projects"() {
        when:
        assembler.arrangeProjects(projects(5), build)

        then:
        build.projects.size() == 5
        build.subprojects.size() == 4

        def subprojects = build.subprojects as List
        build.rootProject.dependencies as List == [subprojects[1], subprojects[2], subprojects[3]]
        subprojects[1].dependencies as List == [subprojects[0]]
        subprojects[2].dependencies as List == [subprojects[0]]
        subprojects[3].dependencies as List == [subprojects[0]]
        subprojects[0].dependencies.empty
    }

    def projects(int p) {
        return new Settings(p, 3)
    }
}
