package org.gradle.builds.assemblers;

import org.gradle.builds.model.Dependency;

import java.util.*;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private int layers = 0;

    public int getLayers() {
        return layers;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    void addNode(Node node) {
        int layer = node.getLayer();
        nodes.add(node);
        if (layer >= layers) {
            layers = layer + 1;
        }
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
        List<Dependency<T>> deps = new ArrayList<>(node.getApiDependencies().size() + node.getImplementationDependencies().size());
        for (Node dep : node.getApiDependencies()) {
            visitNode(dep, visitor, values);
            deps.add(Dependency.api(values.get(dep)));
        }
        for (Node dep : node.getImplementationDependencies()) {
            visitNode(dep, visitor, values);
            deps.add(Dependency.implementation(values.get(dep)));
        }
        T value = visitor.visitNode(node, new NodeDependencies<T>(deps));
        values.put(node, value);
    }

    public static class NodeDependencies<T> implements Iterable<Dependency<T>> {
        private final List<Dependency<T>> deps;

        public NodeDependencies(List<Dependency<T>> deps) {
            this.deps = deps;
        }

        @Override
        public Iterator<Dependency<T>> iterator() {
            return deps.iterator();
        }

        public List<Dependency<T>> getAll() {
            return deps;
        }
    }

    public interface Node {
        /**
         * 0 == shallowest layer
         */
        int getLayer();

        boolean isExported();

        boolean isDeepest();

        boolean isUseAlternate();

        /**
         * A unique name for this node in the graph.
         */
        String getNameSuffix();

        List<? extends Node> getApiDependencies();

        List<? extends Node> getImplementationDependencies();
    }

    public interface Visitor<T> {
        T visitNode(Node node, NodeDependencies<T> dependencies);
    }
}
