package org.gradle.builds.assemblers

import spock.lang.Specification

class GraphAssemblerTest extends Specification {
    def assembler = new GraphAssembler()

    def "arranges 1 node"() {
        // <root>
        when:
        def graph = assembler.arrange(1)

        then:
        graph.nodes.size() == 1
        graph.layers == 1

        def root = graph.nodes[0]
        root.apiDependencies.empty
        root.implementationDependencies.empty
        root.layer == 0
        root.exported
        root.deepest
        root.receiveIncoming
        root.nameSuffix == ''
        !root.useAlternate
    }

    def "arranges 2 nodes"() {
        // <root> -> <node>
        when:
        def graph = assembler.arrange(2)

        then:
        def nodes = graph.nodes
        graph.nodes.size() == 2
        graph.layers == 2

        def root = nodes[0]
        def n1 = nodes[1]

        root.apiDependencies.empty
        root.implementationDependencies == [n1]
        root.layer == 0
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''
        !root.useAlternate

        n1.apiDependencies.empty
        n1.implementationDependencies.empty
        n1.layer == 1
        n1.exported
        n1.receiveIncoming
        n1.deepest
        n1.nameSuffix == ''
        n1.useAlternate
    }

    def "arranges 3 nodes"() {
        // <root> -> <node1> -> <node2>
        when:
        def graph = assembler.arrange(3)

        then:
        def nodes = graph.nodes
        graph.nodes.size() == 3
        graph.layers == 2

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]

        root.apiDependencies.empty
        root.implementationDependencies == [n1]
        root.layer == 0
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''
        !root.useAlternate

        n1.apiDependencies.empty
        n1.implementationDependencies == [n2]
        n1.layer == 1
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == 'Api'
        !n1.useAlternate

        n2.apiDependencies.empty
        n2.implementationDependencies.empty
        n2.layer == 1
        !n2.exported
        !n2.receiveIncoming
        n2.deepest
        n2.nameSuffix == 'Core'
        n2.useAlternate
    }

    def "arranges 4 nodes"() {
        // <root> -> <node1> -> <node3>
        //        -> <node2> ->
        when:
        def graph = assembler.arrange(4)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 4
        graph.layers == 2

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]
        def n3 = nodes[3]

        root.apiDependencies.empty
        root.implementationDependencies == [n1, n2]
        root.layer == 0
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''
        !root.useAlternate

        n1.apiDependencies.empty
        n1.implementationDependencies == [n3]
        n1.layer == 1
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == 'Api1'
        !n1.useAlternate

        n2.apiDependencies.empty
        n2.implementationDependencies == [n3]
        n2.layer == 1
        n2.exported
        n2.receiveIncoming
        !n2.deepest
        n2.nameSuffix == 'Api2'
        n2.useAlternate

        n3.apiDependencies.empty
        n3.implementationDependencies.empty
        n3.layer == 1
        !n3.exported
        !n3.receiveIncoming
        n3.deepest
        n3.nameSuffix == 'Core'
        n3.useAlternate
    }

    def "arranges 5 nodes"() {
        // <root> -> <node1> -> <node3> -> <node4>
        //        -> <node2> ->
        when:
        def graph = assembler.arrange(5)

        then:
        def nodes = graph.nodes
        graph.nodes.size() == 5
        graph.layers == 2

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]
        def n3 = nodes[3]
        def n4 = nodes[4]

        root.apiDependencies.empty
        root.implementationDependencies == [n1, n2]
        root.layer == 0
        root.nameSuffix == ''
        !root.exported
        !root.receiveIncoming
        !root.useAlternate

        n1.apiDependencies.empty
        n1.implementationDependencies == [n3]
        n1.layer == 1
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == 'Api1'
        !n1.useAlternate

        n2.apiDependencies.empty
        n2.implementationDependencies == [n3]
        n2.layer == 1
        n2.exported
        n2.receiveIncoming
        !n2.deepest
        n2.nameSuffix == 'Api2'
        !n2.useAlternate

        n3.apiDependencies.empty
        n3.implementationDependencies == [n4]
        n3.layer == 1
        !n3.exported
        n3.receiveIncoming
        !n3.deepest
        n3.nameSuffix == 'Impl'
        !n3.useAlternate

        n4.apiDependencies.empty
        n4.implementationDependencies.empty
        n4.layer == 1
        !n4.exported
        !n4.receiveIncoming
        n4.deepest
        n4.nameSuffix == 'Core'
        n4.useAlternate
    }

    def "arranges 6 nodes"() {
        // <root> -> <node1> ->                    <node5>
        //        -> <node2> -> <node3> -> <node4>
        when:
        def graph = assembler.arrange(6)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 6
        graph.layers == 3

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]
        def n3 = nodes[3]
        def n4 = nodes[4]
        def n5 = nodes[5]

        root.apiDependencies.empty
        root.implementationDependencies == [n1, n2]
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''
        !root.useAlternate

        n1.apiDependencies == [n5]
        n1.implementationDependencies == [n3]
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == '1Api1'
        !n1.useAlternate

        n2.apiDependencies.empty
        n2.implementationDependencies == [n3, n5]
        n2.exported
        n2.receiveIncoming
        !n2.deepest
        n2.nameSuffix == '1Api2'
        !n2.useAlternate

        n3.apiDependencies.empty
        n3.implementationDependencies == [n4, n5]
        !n3.exported
        n3.receiveIncoming
        !n3.deepest
        n3.nameSuffix == '1Impl'
        !n3.useAlternate

        n4.apiDependencies.empty
        n4.implementationDependencies.empty
        !n4.exported
        !n4.receiveIncoming
        !n4.deepest
        n4.nameSuffix == '1Core'
        n4.useAlternate

        n5.apiDependencies.empty
        n5.implementationDependencies.empty
        !n5.exported
        n5.receiveIncoming
        n5.deepest
        n5.nameSuffix == '2'
        n5.useAlternate
    }

    def "arranges 7 nodes"() {
        // <root> -> <node1> ->                    <node5> -> <node6>
        //        -> <node2> ->
        //                   -> <node3> ->
        //                              -> <node4>
        when:
        def graph = assembler.arrange(7)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 7
        graph.layers == 3

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]
        def n3 = nodes[3]
        def n4 = nodes[4]
        def n5 = nodes[5]
        def n6 = nodes[6]

        root.apiDependencies.empty
        root.implementationDependencies == [n1, n2]
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''
        !root.useAlternate

        n1.apiDependencies == [n5]
        n1.implementationDependencies == [n3]
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == '1Api1'
        !n1.useAlternate

        n2.apiDependencies.empty
        n2.implementationDependencies == [n3, n5]
        n2.exported
        n2.receiveIncoming
        !n2.deepest
        n2.nameSuffix == '1Api2'
        !n2.useAlternate

        n3.apiDependencies.empty
        n3.implementationDependencies == [n4, n5]
        !n3.exported
        n3.receiveIncoming
        !n3.deepest
        n3.nameSuffix == '1Impl'
        !n3.useAlternate

        n4.apiDependencies.empty
        n4.implementationDependencies.empty
        !n4.exported
        !n4.receiveIncoming
        !n4.deepest
        n4.nameSuffix == '1Core'
        n4.useAlternate

        n5.apiDependencies.empty
        n5.implementationDependencies == [n6]
        !n5.exported
        n5.receiveIncoming
        !n5.deepest
        n5.nameSuffix == '2Api'
        !n5.useAlternate

        n6.apiDependencies.empty
        n6.implementationDependencies.empty
        !n6.exported
        !n6.receiveIncoming
        n6.deepest
        n6.nameSuffix == '2Core'
        n6.useAlternate
    }

    def "arranges 8 nodes"() {
        // <root> -> <node1> ->                    <node6> -> <node7>
        //        -> <node2> ->
        //        -> <node3> ->
        //                   -> <node4> ->
        //                              -> <node5>
        when:
        def graph = assembler.arrange(8)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 8
        graph.layers == 3

        def root = nodes[0]
        def n1 = nodes[1]
        def n2 = nodes[2]
        def n3 = nodes[3]
        def n4 = nodes[4]
        def n5 = nodes[5]
        def n6 = nodes[6]
        def n7 = nodes[7]

        root.apiDependencies.empty
        root.implementationDependencies == [n1, n2, n3]
        !root.exported
        !root.receiveIncoming
        !root.deepest
        root.nameSuffix == ''

        n1.apiDependencies == [n6]
        n1.implementationDependencies == [n4]
        n1.exported
        n1.receiveIncoming
        !n1.deepest
        n1.nameSuffix == '1Api1'

        n2.apiDependencies.empty
        n2.implementationDependencies == [n4, n6]
        n2.exported
        n2.receiveIncoming
        !n2.deepest
        n2.nameSuffix == '1Api2'

        n3.apiDependencies.empty
        n3.implementationDependencies == [n4, n6]
        n3.exported
        n3.receiveIncoming
        !n3.deepest
        n3.nameSuffix == '1Api3'

        n4.apiDependencies.empty
        n4.implementationDependencies == [n5, n6]
        !n4.exported
        n4.receiveIncoming
        !n4.deepest
        n4.nameSuffix == '1Impl'

        n5.apiDependencies.empty
        n5.implementationDependencies.empty
        !n5.exported
        !n5.receiveIncoming
        !n5.deepest
        n5.nameSuffix == '1Core'

        n6.apiDependencies.empty
        n6.implementationDependencies == [n7]
        !n6.exported
        n6.receiveIncoming
        !n6.deepest
        n6.nameSuffix == '2Api'

        n7.apiDependencies.empty
        n7.implementationDependencies.empty
        !n7.exported
        !n7.receiveIncoming
        n7.deepest
        n7.nameSuffix == '2Core'
    }

    def "arranges 9 nodes"() {
        // <root> -> <node1> ->                    <node7> -> <node8>
        //        -> <node2> ->
        //        -> <node3> ->
        //                   -> <node4> ->
        //                              -> <node5>
        //                              -> <node6>
        when:
        def graph = assembler.arrange(9)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 9
        graph.layers == 3

        nodes[0].apiDependencies.empty
        nodes[0].implementationDependencies == [nodes[1], nodes[2], nodes[3]]
        nodes[1].apiDependencies == [nodes[7]]
        nodes[1].implementationDependencies == [nodes[4]]
        nodes[2].apiDependencies.empty
        nodes[2].implementationDependencies == [nodes[4], nodes[7]]
        nodes[3].apiDependencies.empty
        nodes[3].implementationDependencies == [nodes[4], nodes[7]]
        nodes[4].apiDependencies.empty
        nodes[4].implementationDependencies == [nodes[5], nodes[6], nodes[7]]
        nodes[5].apiDependencies.empty
        nodes[5].implementationDependencies.empty
        nodes[5].nameSuffix == '1Core1'
        nodes[6].apiDependencies.empty
        nodes[6].implementationDependencies.empty
        nodes[6].nameSuffix == '1Core2'
        nodes[7].apiDependencies.empty
        nodes[7].implementationDependencies == [nodes[8]]
        nodes[8].apiDependencies.empty
        nodes[8].implementationDependencies.empty
    }

    def "arranges 10 nodes"() {
        // <root> -> <node1> ->                    <node7> -> <node9>
        //        -> <node2> ->                    <node8> ->
        //        -> <node3> ->
        //                   -> <node4> ->
        //                              -> <node5>
        //                              -> <node6>
        when:
        def graph = assembler.arrange(10)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 10
        graph.layers == 3

        nodes[0].apiDependencies.empty
        nodes[0].implementationDependencies == [nodes[1], nodes[2], nodes[3]]
        nodes[1].apiDependencies == [nodes[7]]
        nodes[1].implementationDependencies == [nodes[4], nodes[8]]
        nodes[2].apiDependencies.empty
        nodes[2].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        nodes[3].apiDependencies.empty
        nodes[3].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        nodes[4].apiDependencies.empty
        nodes[4].implementationDependencies == [nodes[5], nodes[6], nodes[7], nodes[8]]
        nodes[5].apiDependencies.empty
        nodes[5].implementationDependencies.empty
        nodes[6].apiDependencies.empty
        nodes[6].implementationDependencies.empty
        nodes[7].apiDependencies.empty
        nodes[7].implementationDependencies == [nodes[9]]
        nodes[8].apiDependencies.empty
        nodes[8].implementationDependencies == [nodes[9]]
        nodes[9].apiDependencies.empty
        nodes[9].implementationDependencies.empty
    }

    def "arranges 11 nodes"() {
        // <root> -> <node1> ->                    <node7> ->
        //        -> <node2> ->                    <node8> ->
        //        -> <node3> ->                            -> <node9> -> <node10>
        //                   -> <node4> ->
        //                              -> <node5>
        //                              -> <node6>
        when:
        def graph = assembler.arrange(11)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 11
        graph.layers == 3

        nodes[0].apiDependencies.empty
        nodes[0].implementationDependencies == [nodes[1], nodes[2], nodes[3]]
        !nodes[0].useAlternate
        nodes[1].apiDependencies == [nodes[7]]
        nodes[1].implementationDependencies == [nodes[4], nodes[8]]
        !nodes[1].useAlternate
        nodes[2].apiDependencies.empty
        nodes[2].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        !nodes[2].useAlternate
        nodes[3].apiDependencies.empty
        nodes[3].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        !nodes[3].useAlternate
        nodes[4].apiDependencies.empty
        nodes[4].implementationDependencies == [nodes[5], nodes[6], nodes[7], nodes[8]]
        !nodes[4].useAlternate
        nodes[5].apiDependencies.empty
        nodes[5].implementationDependencies.empty
        !nodes[5].useAlternate
        nodes[6].apiDependencies.empty
        nodes[6].implementationDependencies.empty
        nodes[6].useAlternate
        nodes[7].apiDependencies.empty
        nodes[7].implementationDependencies == [nodes[9]]
        !nodes[7].useAlternate
        nodes[8].apiDependencies.empty
        nodes[8].implementationDependencies == [nodes[9]]
        !nodes[8].useAlternate
        nodes[9].apiDependencies.empty
        nodes[9].implementationDependencies == [nodes[10]]
        !nodes[9].useAlternate
        nodes[10].apiDependencies.empty
        nodes[10].implementationDependencies.empty
        nodes[10].useAlternate
    }

    def "arranges 12 nodes"() {
        // <root> -> <node1> ->                    <node7> ->                     <node11>
        //        -> <node2> ->                    <node8> ->
        //        -> <node3> ->                            -> <node9> ->
        //                   -> <node4> ->                            -> <node10>
        //                              -> <node5>
        //                              -> <node6>
        when:
        def graph = assembler.arrange(12)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 12
        graph.layers == 4

        nodes[0].apiDependencies.empty
        nodes[0].implementationDependencies == [nodes[1], nodes[2], nodes[3]]
        nodes[1].apiDependencies == [nodes[7]]
        nodes[1].implementationDependencies == [nodes[4], nodes[8]]
        nodes[2].apiDependencies.empty
        nodes[2].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        nodes[3].apiDependencies.empty
        nodes[3].implementationDependencies == [nodes[4], nodes[7], nodes[8]]
        nodes[4].apiDependencies.empty
        nodes[4].implementationDependencies == [nodes[5], nodes[6], nodes[7], nodes[8]]
        nodes[5].apiDependencies.empty
        nodes[5].implementationDependencies.empty
        nodes[5].nameSuffix == '1Core1'
        nodes[6].apiDependencies.empty
        nodes[6].implementationDependencies.empty
        nodes[6].nameSuffix == '1Core2'
        nodes[7].apiDependencies == [nodes[11]]
        nodes[7].implementationDependencies == [nodes[9]]
        nodes[8].apiDependencies.empty
        nodes[8].implementationDependencies == [nodes[9], nodes[11]]
        nodes[9].apiDependencies.empty
        nodes[9].implementationDependencies == [nodes[10], nodes[11]]
        nodes[9].nameSuffix == '2Impl'
        nodes[10].apiDependencies.empty
        nodes[10].implementationDependencies.empty
        nodes[10].nameSuffix == '2Core'
        nodes[11].apiDependencies.empty
        nodes[11].implementationDependencies.empty
    }

    def "arranges 17 nodes"() {
        // <root> -> <node1> ->                      <node7> ->                      <node13> ->
        //        -> <node2> ->                      <node8> ->                      <node14> ->
        //        -> <node3> ->                      <node9> ->                               -> <node15> -> <node16>
        //                   -> <node4> ->                   -> <node10> ->
        //                              -> <node5>                       -> <node11>
        //                              -> <node6>                       -> <node12>
        when:
        def graph = assembler.arrange(17)

        then:
        def nodes = graph.nodes as List
        graph.nodes.size() == 17
        graph.layers == 4

        nodes[0].apiDependencies.empty
        nodes[0].implementationDependencies == [nodes[1], nodes[2], nodes[3]]
        nodes[1].apiDependencies == [nodes[7]]
        nodes[1].implementationDependencies == [nodes[4], nodes[8], nodes[9]]
        nodes[2].apiDependencies.empty
        nodes[2].implementationDependencies == [nodes[4], nodes[7], nodes[8], nodes[9]]
        nodes[3].apiDependencies.empty
        nodes[3].implementationDependencies == [nodes[4], nodes[7], nodes[8], nodes[9]]
        nodes[4].apiDependencies.empty
        nodes[4].implementationDependencies == [nodes[5], nodes[6], nodes[7], nodes[8], nodes[9]]
        nodes[5].apiDependencies.empty
        nodes[5].implementationDependencies.empty
        nodes[6].apiDependencies.empty
        nodes[6].implementationDependencies.empty
        nodes[7].apiDependencies == [nodes[13]]
        nodes[7].implementationDependencies == [nodes[10], nodes[14]]
        nodes[8].apiDependencies.empty
        nodes[8].implementationDependencies == [nodes[10], nodes[13], nodes[14]]
        nodes[9].apiDependencies.empty
        nodes[9].implementationDependencies == [nodes[10], nodes[13], nodes[14]]
        nodes[10].apiDependencies.empty
        nodes[10].implementationDependencies == [nodes[11], nodes[12], nodes[13], nodes[14]]
        nodes[11].apiDependencies.empty
        nodes[11].implementationDependencies.empty
        nodes[12].apiDependencies.empty
        nodes[12].implementationDependencies.empty
        nodes[13].apiDependencies.empty
        nodes[13].implementationDependencies == [nodes[15]]
        nodes[14].apiDependencies.empty
        nodes[14].implementationDependencies == [nodes[15]]
        nodes[15].apiDependencies.empty
        nodes[15].implementationDependencies == [nodes[16]]
        nodes[16].apiDependencies.empty
        nodes[16].implementationDependencies.empty
        nodes[16].deepest
    }

    def "arranges n nodes"() {
        when:
        def graph = assembler.arrange(n)

        then:
        graph.nodes.size() == n

        where:
        n << (13..200)
    }
}
