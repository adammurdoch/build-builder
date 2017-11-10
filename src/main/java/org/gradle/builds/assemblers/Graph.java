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

    public <T> void visit(Visitor<T> visitor) {
        Map<Node, T> values = new HashMap<>();
        int lastLayer = getLayers().size() - 1;
        for (int layer = lastLayer; layer >= 0; layer--) {
            List<? extends Node> nodes = getLayers().get(layer);
            for (Node node : nodes) {
                Set<T> deps = new LinkedHashSet<>();
                for (Node dep : node.getDependencies()) {
                    deps.add(values.get(dep));
                }
                T value = visitor.visitNode(node, deps);
                values.put(node, value);
            }
        }
    }

    public interface Node {
        int getLayer();

        int getItem();

        boolean isUseAlternate();

        boolean isLastLayer();

        List<? extends Node> getDependencies();
    }

    public interface Visitor<T> {
        T visitNode(Node node, Set<T> dependencies);
    }
}
