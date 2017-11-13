package org.gradle.builds.assemblers

import org.gradle.builds.model.Build
import org.gradle.builds.model.Project
import spock.lang.Specification

import java.nio.file.Paths

class StructureAssemblerTest extends Specification {
    def assembler = new StructureAssembler()
    def initializer = new ProjectInitializer() {
        @Override
        void initRootProject(Project project) {
        }

        @Override
        void initLibraryProject(Project project) {
        }
    }
    def build = new Build(Paths.get("dir"), "testApp")

    def "builds dependency graph with one project"() {
        when:
        build.settings = projects(1)
        assembler.arrangeProjects(build, initializer)

        then:
        build.projects.size() == 1
        build.subprojects.empty
        build.rootProject.requiredProjects.empty
    }

    def "builds dependency graph with two projects"() {
        when:
        build.settings = projects(2)
        assembler.arrangeProjects(build, initializer)

        then:
        build.projects.size() == 2
        build.subprojects.size() == 1

        def subprojects = build.subprojects as List
        impl(build.rootProject) == subprojects
    }

    def "builds dependency graph with three projects"() {
        when:
        build.settings = projects(3)
        assembler.arrangeProjects(build, initializer)

        then:
        build.projects.size() == 3
        build.subprojects.size() == 2

        def subprojects = build.subprojects as List
        api(build.rootProject).empty
        impl(build.rootProject) == [subprojects[1]]
        api(subprojects[1]).empty
        impl(subprojects[1]) == [subprojects[0]]
        api(subprojects[0]).empty
        impl(subprojects[0]).empty
    }

    def "builds dependency graph with four projects"() {
        when:
        build.settings = projects(4)
        assembler.arrangeProjects(build, initializer)

        then:
        build.projects.size() == 4
        build.subprojects.size() == 3

        def subprojects = build.subprojects as List
        api(build.rootProject) == [subprojects[1]]
        impl(build.rootProject) == [subprojects[2]]
        api(subprojects[1]).empty
        impl(subprojects[1]) == [subprojects[0]]
        api(subprojects[2]).empty
        impl(subprojects[2]) == [subprojects[0]]
        api(subprojects[0]).empty
        impl(subprojects[0]).empty
    }

    def "builds dependency graph with five projects"() {
        when:
        build.settings = projects(5)
        assembler.arrangeProjects(build, initializer)

        then:
        build.projects.size() == 5
        build.subprojects.size() == 4

        def subprojects = build.subprojects as List
        api(build.rootProject) == [subprojects[2]]
        impl(build.rootProject) == [subprojects[3]]
        api(subprojects[2]).empty
        impl(subprojects[2]) == [subprojects[0], subprojects[1]]
        api(subprojects[3]).empty
        impl(subprojects[3]) == [subprojects[0], subprojects[1]]
        api(subprojects[0]).empty
        impl(subprojects[0]).empty
        api(subprojects[1]).empty
        impl(subprojects[1]).empty
    }

    def projects(int p) {
        return new Settings(p, 3)
    }

    def api(Project p) {
        return p.requiredProjects.findAll { it.api }*.target
    }

    def impl(Project p) {
        return p.requiredProjects.findAll { !it.api }*.target
    }
}
