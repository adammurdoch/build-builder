package org.gradle.builds.assemblers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphAssembler {
    /**
     * Attempts to create 3 layers, then attempts to keep between 3 and 6 nodes in each layer
     * A node depends on every node in the next layer.
     */
    public Graph arrange(int nodes) {
        List<Layer> layers = new ArrayList<>();
        layers.add(new Layer(layers.size(), 1));
        int remaining = nodes - 1;
        while (remaining > 0) {
            if (remaining > 8) {
                layers.add(new Layer(layers.size(), 6));
                remaining -= 6;
                continue;
            }
            switch (remaining) {
                case 1:
                    layers.add(new Layer(layers.size(), 1));
                    break;
                case 2:
                    layers.add(new Layer(layers.size(), 1));
                    layers.add(new Layer(layers.size(), 1));
                    break;
                case 3:
                    layers.add(new Layer(layers.size(), 2));
                    layers.add(new Layer(layers.size(), 1));
                    break;
                case 4:
                    layers.add(new Layer(layers.size(), 3));
                    layers.add(new Layer(layers.size(), 1));
                    break;
                case 5:
                    layers.add(new Layer(layers.size(), 3));
                    layers.add(new Layer(layers.size(), 2));
                    break;
                case 6:
                    layers.add(new Layer(layers.size(), 4));
                    layers.add(new Layer(layers.size(), 2));
                    break;
                case 7:
                    layers.add(new Layer(layers.size(), 5));
                    layers.add(new Layer(layers.size(), 2));
                    break;
                case 8:
                    layers.add(new Layer(layers.size(), 6));
                    layers.add(new Layer(layers.size(), 2));
                    break;
            }
            remaining = 0;
        }

        Graph graph = new Graph();
        layers.get(0).noAlternate = true;
        if (nodes == 2) {
            layers.get(1).noAlternate = true;
        }
        for (Layer layer : layers) {
            if (layer.id < layers.size() - 1) {
                layer.next = layers.get(layer.id + 1);
            }
        }
        for (Layer layer : layers) {
            for (NodeImpl node : layer.getNodes()) {
                graph.addNode(node);
            }
        }
        return graph;
    }

    private static class NodeImpl implements Graph.Node {
        final Layer layer;
        final int item;
        final boolean useAlternate;
        final List<NodeImpl> dependencies;

        NodeImpl(Layer layer, int item, List<NodeImpl> dependencies, boolean useAlternate) {
            this.layer = layer;
            this.item = item;
            this.dependencies = dependencies;
            this.useAlternate = useAlternate;
        }

        @Override
        public String toString() {
            return "{layer: " + (layer.id + 1) + ", item: " + (item + 1) + "}";
        }

        @Override
        public int getLayer() {
            return layer.id;
        }

        @Override
        public int getItem() {
            return item;
        }

        @Override
        public boolean isUseAlternate() {
            return useAlternate;
        }

        @Override
        public boolean isLastLayer() {
            return layer.isLast();
        }

        @Override
        public List<? extends Graph.Node> getDependencies() {
            return dependencies;
        }
    }

    private static class Layer {
        final int id;
        final int size;
        final List<NodeImpl> nodes;
        Layer next;
        boolean noAlternate;

        Layer(int id, int size) {
            this.id = id;
            this.size = size;
            this.nodes = new ArrayList<>(size);
        }

        boolean isLast() {
            return next == null;
        }

        List<NodeImpl> getNodes() {
            List<NodeImpl> deps = next == null ? Collections.emptyList() : next.getNodes();
            if (this.nodes.size() != size) {
                for (int item = 0; item < size; item++) {
                    this.nodes.add(new NodeImpl(this, item, deps, size > 1 && item == size - 1 || !noAlternate && size == 1 && isLast()));
                }
            }
            return this.nodes;
        }
    }
}
