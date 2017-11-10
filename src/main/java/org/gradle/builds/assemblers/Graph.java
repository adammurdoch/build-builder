package org.gradle.builds.assemblers;

import java.util.*;

public class Graph {
    private final List<NodeDetails> nodes = new ArrayList<>();
    private final List<List<NodeDetails>> layers = new ArrayList<>();
    private NodeDetails root;

    public List<List<NodeDetails>> getLayers() {
        return layers;
    }

    public NodeDetails getRoot() {
        return root;
    }

    public List<NodeDetails> getNodes() {
        return nodes;
    }

    void addNode(NodeDetails node) {
        int layer = node.getLayer();
        if (layer == 0 && nodes.isEmpty()) {
            root = node;
        }
        nodes.add(node);
        while (layers.size() <= node.getLayer()) {
            layers.add(new ArrayList<>());
        }
        layers.get(layer).add(node);
    }

    public <T> void visit(Visitor<T> visitor) {
        Map<NodeDetails, T> values = new HashMap<>();
        int lastLayer = getLayers().size() - 1;
        for (int layer = lastLayer; layer >= 0; layer--) {
            List<? extends NodeDetails> nodes = getLayers().get(layer);
            for (NodeDetails node : nodes) {
                Set<T> deps = new LinkedHashSet<>();
                for (NodeDetails dep : node.getDependencies()) {
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

        List<? extends NodeDetails> getDependencies();
    }

    public interface Visitor<T> {
        T visitNode(NodeDetails node, Set<T> dependencies);
    }
}
