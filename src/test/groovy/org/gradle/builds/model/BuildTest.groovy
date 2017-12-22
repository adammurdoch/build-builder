package org.gradle.builds.model

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Path

class BuildTest extends Specification {
    @Rule
    TemporaryFolder tmpDir = new TemporaryFolder()
    Path rootDir
    Build build

    def setup() {
        rootDir = tmpDir.newFolder().toPath()
        build = new Build(rootDir, "root", "testApp")
    }

    def "adds root project"() {
        expect:
        build.projects.size() == 1
        build.subprojects.size() == 0

        build.rootProject.path == ':'
        build.rootProject.name == 'testApp'
        build.rootProject.projectDir == rootDir
        build.rootProject.parent == null
    }

    def "adds project given name"() {
        when:
        def p = build.addProject("lib1")

        then:
        p.name == 'lib1'
        p.path == ':lib1'
        p.projectDir == rootDir.resolve('lib1')
        p.parent == build.rootProject

        build.projects == [build.rootProject, p] as Set
        build.subprojects == [p] as Set
    }

    def "adds project given path and project dir"() {
        def projectDir1 = rootDir.resolve('projects/lib1')
        def projectDir2 = rootDir.resolve('projects/lib1/util')
        def projectDir3 = rootDir.resolve('projects/lib2/util')

        when:
        def p1 = build.addProject(':lib1', projectDir1)
        def p2 = build.addProject(':lib1:util', projectDir2)
        def p3 = build.addProject(':lib2:util', projectDir3)

        then:
        p1.name == 'lib1'
        p1.path == ':lib1'
        p1.projectDir == projectDir1
        p1.parent == build.rootProject

        p2.name == 'util'
        p2.path == ':lib1:util'
        p2.projectDir == projectDir2
        p2.parent == p1

        def p4 = build.projects.find { it.path == ':lib2' }
        p4.name == 'lib2'
        p4.projectDir == rootDir.resolve('lib2')
        p4.parent == build.rootProject

        p3.name == 'util'
        p3.path == ':lib2:util'
        p3.projectDir == projectDir3
        p3.parent == p4

        build.projects == [build.rootProject, p1, p2, p3, p4] as Set
        build.subprojects == [p1, p2, p3, p4] as Set
    }
}
