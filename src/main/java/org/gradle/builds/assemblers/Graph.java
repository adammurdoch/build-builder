package org.gradle.builds.assemblers;

import java.util.*;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<List<Node>> layers = new ArrayList<>();

    public List<List<Node>> getLayers() {
        return layers;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    void addNode(Node node) {
        int layer = node.getLayer();
        nodes.add(node);
        while (layers.size() <= node.getLayer()) {
            layers.add(new ArrayList<>());
        }
        layers.get(layer).add(node);
    }

    /**
     * Visits the nodes of this graph, in dependency-first order.
     */
    public <T> void visit(Visitor<T> visitor) {
        Map<Node, T> values = new HashMap<>();
        for (Node node : nodes) {
            visitNode(node, visitor, values);
        }
    }

    private <T> void visitNode(Node node, Visitor<T> visitor, Map<Node, T> values) {
        if (values.containsKey(node)) {
            return;
        }
        Set<T> deps = new LinkedHashSet<>();
        for (Node dep : node.getDependencies()) {
            visitNode(dep, visitor, values);
            deps.add(values.get(dep));
        }
        T value = visitor.visitNode(node, deps);
        values.put(node, value);
    }

    public interface Node {
        int getLayer();

        boolean isUseAlternate();

        /**
         * A unique name for this node in the graph.
         */
        String getNameSuffix();

        List<? extends Node> getDependencies();
    }

    public interface Visitor<T> {
        T visitNode(Node node, Set<T> dependencies);
    }
}
