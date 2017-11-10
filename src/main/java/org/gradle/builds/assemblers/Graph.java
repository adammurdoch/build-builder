package org.gradle.builds.assemblers;

import java.util.*;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final List<List<Node>> layers = new ArrayList<>();
    private final Node root = new Node(0, 0, false) {
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

    void addNode(NodeDetails nodeDetails) {
        Node node;
        int layer = nodeDetails.getLayer();
        int item = nodeDetails.getItem();
        if (nodeDetails.getLayer() == 0 && nodes.isEmpty()) {
            node = root;
            nodes.add(root);
        } else {
            node = new Node(layer, item, nodeDetails.isUseAlternate()) {
                @Override
                public String toString() {
                    return "{layer: " + (nodeDetails.getLayer() + 1) + ", item: " + (item + 1) + "}";
                }
            };
            nodes.add(node);
        }
        while (layers.size() <= nodeDetails.getLayer()) {
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
                T value = visitor.visitNode(node, deps);
                values.put(node, value);
            }
        }
    }

    public interface NodeDetails {
        int getLayer();

        int getItem();

        boolean isUseAlternate();

        boolean isLastLayer();
    }

    public interface Visitor<T> {
        T visitNode(NodeDetails node, Set<T> dependencies);
    }

    private class Node implements NodeDetails {
        private final int layer;
        private final int item;
        private final boolean useAlternate;

        Node(int layer, int item, boolean useAlternate) {
            this.layer = layer;
            this.item = item;
            this.useAlternate = useAlternate;
        }

        @Override
        public int getLayer() {
            return layer;
        }

        @Override
        public int getItem() {
            return item;
        }

        @Override
        public boolean isLastLayer() {
            return layer == getLayers().size() - 1;
        }

        @Override
        public boolean isUseAlternate() {
            return useAlternate;
        }

        public List<Node> getDependsOn() {
            if (layer < layers.size() - 1) {
                return layers.get(layer + 1);
            }
            return Collections.emptyList();
        }
    }
}
