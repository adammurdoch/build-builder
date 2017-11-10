package org.gradle.builds.assemblers;

import java.util.ArrayList;
import java.util.List;

public class GraphAssembler {
    /**
     * Attempts to create 3 layers, then attempts to keep between 3 and 6 nodes in each layer
     * A node depends on every node in the next layer.
     */
    public Graph arrange(int nodes) {
        int remaining = nodes - 1;
        List<Layer> layers = new ArrayList<>();
        layers.add(new Layer(layers.size(), 1));
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
        for (Layer layer : layers) {
            if (layer.id < layers.size() - 1) {
                layer.next = layers.get(layer.id + 1);
            }
            int itemsInLayer = layer.nodes;
            for (int item = 0; item < itemsInLayer; item++) {
                graph.addNode(new NodeImpl(layer, item, nodes > 3 && item == itemsInLayer - 1 || nodes == 3 && layer.isLast()));
            }
        }
        return graph;
    }

    private static class NodeImpl implements Graph.NodeDetails {
        final Layer layer;
        final int item;
        final boolean useAlternate;

        NodeImpl(Layer layer, int item, boolean useAlternate) {
            this.layer = layer;
            this.item = item;
            this.useAlternate = useAlternate;
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
    }

    private static class Layer {
        final int id;
        final int nodes;
        Layer next;

        Layer(int id, int nodes) {
            this.id = id;
            this.nodes = nodes;
        }

        boolean isLast() {
            return next == null;
        }
    }
}
