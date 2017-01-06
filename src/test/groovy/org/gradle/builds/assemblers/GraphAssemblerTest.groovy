package org.gradle.builds.assemblers

import spock.lang.Specification

class GraphAssemblerTest extends Specification {
    def assembler = new GraphAssembler()

    def "arranges 1 node"() {
        // <root>
        def graph = new Graph()

        when:
        assembler.arrange(1, graph)

        then:
        graph.nodes.size() == 1
        graph.layers.size() == 1

        graph.root.dependsOn.empty
    }

    def "arranges 2 nodes"() {
        // <root> -> <node>
        def graph = new Graph()

        when:
        assembler.arrange(2, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 2
        graph.layers.size() == 2

        graph.root.dependsOn as List == [nodes[1]]
        nodes[1].dependsOn.empty
    }

    def "arranges 3 nodes"() {
        // <root> -> <node1> -> <node2>
        def graph = new Graph()

        when:
        assembler.arrange(3, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 3
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1]]
        nodes[1].dependsOn == [nodes[2]]
        nodes[2].dependsOn.empty
    }

    def "arranges 4 nodes"() {
        // <root> -> <node1> -> <node3>
        //        -> <node2> ->
        def graph = new Graph()

        when:
        assembler.arrange(4, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 4
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2]]
        nodes[1].dependsOn == [nodes[3]]
        nodes[2].dependsOn == [nodes[3]]
        nodes[3].dependsOn.empty
    }

    def "arranges 5 nodes"() {
        // <root> -> <node1> -> <node4>
        //        -> <node2> ->
        //        -> <node3> ->
        def graph = new Graph()

        when:
        assembler.arrange(5, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 5
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3]]
        nodes[1].dependsOn == [nodes[4]]
        nodes[2].dependsOn == [nodes[4]]
        nodes[3].dependsOn == [nodes[4]]
        nodes[4].dependsOn.empty
    }

    def "arranges 6 nodes"() {
        // <root> -> <node1> -> <node4> (no-deps)
        //        -> <node2> -> <node5> (no-deps
        //        -> <node3> ->
        def graph = new Graph()

        when:
        assembler.arrange(6, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 6
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3]]
        nodes[1].dependsOn == [nodes[4], nodes[5]]
        nodes[2].dependsOn == [nodes[4], nodes[5]]
        nodes[3].dependsOn == [nodes[4], nodes[5]]
        nodes[4].dependsOn.empty
        nodes[5].dependsOn.empty
    }

    def "arranges 7 nodes"() {
        // <root> -> <node1> -> <node5> (no-deps)
        //        -> <node2> -> <node6> (no-deps)
        //        -> <node3> ->
        //        -> <node4> ->
        def graph = new Graph()

        when:
        assembler.arrange(7, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 7
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4]]
        nodes[1].dependsOn == [nodes[5], nodes[6]]
        nodes[2].dependsOn == [nodes[5], nodes[6]]
        nodes[3].dependsOn == [nodes[5], nodes[6]]
        nodes[4].dependsOn == [nodes[5], nodes[6]]
        nodes[5].dependsOn.empty
        nodes[6].dependsOn.empty
    }

    def "arranges 8 nodes"() {
        // <root> -> <node1> -> <node6> (no-deps)
        //        -> <node2> -> <node7> (no-deps)
        //        -> <node3> ->
        //        -> <node4> ->
        //        -> <node5> ->
        def graph = new Graph()

        when:
        assembler.arrange(8, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 8
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5]]
        nodes[1].dependsOn == [nodes[6], nodes[7]]
        nodes[2].dependsOn == [nodes[6], nodes[7]]
        nodes[3].dependsOn == [nodes[6], nodes[7]]
        nodes[4].dependsOn == [nodes[6], nodes[7]]
        nodes[5].dependsOn == [nodes[6], nodes[7]]
        nodes[6].dependsOn.empty
        nodes[7].dependsOn.empty
    }

    def "arranges 9 nodes"() {
        // <root> -> <node1> -> <node7> (no-deps)
        //        -> <node2> -> <node8> (no-deps)
        //        -> <node3> ->
        //        -> <node4> ->
        //        -> <node5> ->
        //        -> <node6> ->
        def graph = new Graph()

        when:
        assembler.arrange(9, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 9
        graph.layers.size() == 3

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5], nodes[6]]
        nodes[1].dependsOn == [nodes[7], nodes[8]]
        nodes[2].dependsOn == [nodes[7], nodes[8]]
        nodes[3].dependsOn == [nodes[7], nodes[8]]
        nodes[4].dependsOn == [nodes[7], nodes[8]]
        nodes[5].dependsOn == [nodes[7], nodes[8]]
        nodes[6].dependsOn == [nodes[7], nodes[8]]
        nodes[7].dependsOn.empty
        nodes[8].dependsOn.empty
    }

    def "arranges 10 nodes"() {
        // <root> -> <node1> -> <node7> -> <node9> (no-deps)
        //        -> <node2> -> <node8> ->
        //        -> <node3> ->
        //        -> <node4> ->
        //        -> <node5> ->
        //        -> <node6> ->
        def graph = new Graph()

        when:
        assembler.arrange(10, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 10
        graph.layers.size() == 4

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5], nodes[6]]
        nodes[1].dependsOn == [nodes[7], nodes[8]]
        nodes[2].dependsOn == [nodes[7], nodes[8]]
        nodes[3].dependsOn == [nodes[7], nodes[8]]
        nodes[4].dependsOn == [nodes[7], nodes[8]]
        nodes[5].dependsOn == [nodes[7], nodes[8]]
        nodes[6].dependsOn == [nodes[7], nodes[8]]
        nodes[7].dependsOn == [nodes[9]]
        nodes[8].dependsOn == [nodes[9]]
        nodes[9].dependsOn.empty
    }

    def "arranges 11 nodes"() {
        // <root> -> <node1> -> <node7> -> <node10> (no-deps)
        //        -> <node2> -> <node8> ->
        //        -> <node3> -> <node9> ->
        //        -> <node4> ->
        //        -> <node5> ->
        //        -> <node6> ->
        def graph = new Graph()

        when:
        assembler.arrange(11, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 11
        graph.layers.size() == 4

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5], nodes[6]]
        nodes[1].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[2].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[3].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[4].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[5].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[6].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[7].dependsOn == [nodes[10]]
        nodes[8].dependsOn == [nodes[10]]
        nodes[9].dependsOn == [nodes[10]]
        nodes[10].dependsOn.empty
    }

    def "arranges 12 nodes"() {
        // <root> -> <node1> -> <node7> -> <node10> (no-deps)
        //        -> <node2> -> <node8> -> <node11> (no-deps)
        //        -> <node3> -> <node9> ->
        //        -> <node4> ->
        //        -> <node5> ->
        //        -> <node6> ->
        def graph = new Graph()

        when:
        assembler.arrange(12, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 12
        graph.layers.size() == 4

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5], nodes[6]]
        nodes[1].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[2].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[3].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[4].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[5].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[6].dependsOn == [nodes[7], nodes[8], nodes[9]]
        nodes[7].dependsOn == [nodes[10], nodes[11]]
        nodes[8].dependsOn == [nodes[10], nodes[11]]
        nodes[9].dependsOn == [nodes[10], nodes[11]]
        nodes[10].dependsOn.empty
        nodes[11].dependsOn.empty
    }

    def "arranges 17 nodes"() {
        // <root> -> <node1> -> <node7> -> <node13> -> <node16> (no-deps)
        //        -> <node2> -> <node8> -> <node14> ->
        //        -> <node3> -> <node9> -> <node15> ->
        //        -> <node4> -> <node10> ->
        //        -> <node5> -> <node11> ->
        //        -> <node6> -> <node12> ->
        def graph = new Graph()

        when:
        assembler.arrange(17, graph)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 17
        graph.layers.size() == 5

        graph.root.dependsOn as List == [nodes[1], nodes[2], nodes[3], nodes[4], nodes[5], nodes[6]]
        nodes[1].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[2].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[3].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[4].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[5].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[6].dependsOn == [nodes[7], nodes[8], nodes[9], nodes[10], nodes[11], nodes[12]]
        nodes[7].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[8].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[9].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[10].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[11].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[12].dependsOn == [nodes[13], nodes[14], nodes[15]]
        nodes[13].dependsOn == [nodes[16]]
        nodes[14].dependsOn == [nodes[16]]
        nodes[15].dependsOn == [nodes[16]]
        nodes[16].dependsOn.empty
    }
}