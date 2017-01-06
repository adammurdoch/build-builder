package org.gradle.builds.assemblers;

import java.util.*;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<List<Node>> layers = new ArrayList<>();
    private final Node root = new Node(0) {
        @Override
        public String toString() {
            return "<root>";
        }
    };

    public List<List<Node>> getLayers() {
        return layers;
    }

    public Node getRoot() {
        return root;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    void addNode(int layer, int item) {
        Node node;
        if (layer == 0 && nodes.isEmpty()) {
            node = root;
            nodes.add(root);
        } else {
            node = new Node(layer) {
                @Override
                public String toString() {
                    return "{layer: " + (layer + 1) + ", item: " + (item + 1) + "}";
                }
            };
            nodes.add(node);
        }
        while (layers.size() <= layer) {
            layers.add(new ArrayList<>());
        }
        layers.get(layer).add(node);
    }

    public <T> void visit(Visitor<T> visitor) {
        Map<Graph.Node, T> values = new HashMap<>();
        int lastLayer = getLayers().size() - 1;
        for (int layer = lastLayer; layer >= 0; layer--) {
            List<Graph.Node> nodes = getLayers().get(layer);
            for (int item = 0; item < nodes.size(); item++) {
                Graph.Node node = nodes.get(item);
                Set<T> deps = new LinkedHashSet<T>();
                for (Node dep : node.getDependsOn()) {
                    deps.add(values.get(dep));
                }
                T value = visitor.visitNode(layer, item, lastLayer == layer, deps);
                values.put(node, value);
            }
        }
    }

    public interface Visitor<T> {
        T visitNode(int layer, int item, boolean lastLayer, Set<T> dependencies);
    }

    public class Node {
        private final int layer;

        public Node(int layer) {
            this.layer = layer;
        }

        public List<Node> getDependsOn() {
            if (layer < layers.size()-1) {
                return layers.get(layer + 1);
            }
            return Collections.emptyList();
        }
    }
}
