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
        layers.add(new Layer(1));
        while (remaining > 0) {
            if (remaining > 8) {
                layers.add(new Layer(6));
                remaining -= 6;
                continue;
            }
            switch (remaining) {
                case 1:
                    layers.add(new Layer(1));
                    break;
                case 2:
                    layers.add(new Layer(1));
                    layers.add(new Layer(1));
                    break;
                case 3:
                    layers.add(new Layer(2));
                    layers.add(new Layer(1));
                    break;
                case 4:
                    layers.add(new Layer(3));
                    layers.add(new Layer(1));
                    break;
                case 5:
                    layers.add(new Layer(3));
                    layers.add(new Layer(2));
                    break;
                case 6:
                    layers.add(new Layer(4));
                    layers.add(new Layer(2));
                    break;
                case 7:
                    layers.add(new Layer(5));
                    layers.add(new Layer(2));
                    break;
                case 8:
                    layers.add(new Layer(6));
                    layers.add(new Layer(2));
                    break;
            }
            remaining = 0;
        }

        Graph graph = new Graph();
        for (int layer = 0; layer < layers.size(); layer++) {
            int itemsInLayer = layers.get(layer).nodes;
            for (int item = 0; item < itemsInLayer; item++) {
                graph.addNode(layer, item, nodes > 3 && item == itemsInLayer - 1 || nodes == 3 && layer == 2);
            }
        }
        return graph;
    }

    private static class Layer {
        final int nodes;

        Layer(int nodes) {
            this.nodes = nodes;
        }
    }
}
