package org.gradle.builds.assemblers;

import java.util.ArrayList;
import java.util.List;

public class GraphAssembler {
    /**
     * Attempts to create 3 layers, then attempts to keep between 3 and 6 nodes in each layer
     * A node depends on every node in the next layer.
     */
    public void arrange(int nodes, Graph graph) {
        int remaining = nodes - 1;
        List<Integer> layers = new ArrayList<>();
        layers.add(1);
        while (remaining > 0) {
            if (remaining > 8) {
                layers.add(6);
                remaining -= 6;
                continue;
            }
            switch (remaining) {
                case 1:
                    layers.add(1);
                    break;
                case 2:
                    layers.add(1);
                    layers.add(1);
                    break;
                case 3:
                    layers.add(2);
                    layers.add(1);
                    break;
                case 4:
                    layers.add(3);
                    layers.add(1);
                    break;
                case 5:
                    layers.add(3);
                    layers.add(2);
                    break;
                case 6:
                    layers.add(4);
                    layers.add(2);
                    break;
                case 7:
                    layers.add(5);
                    layers.add(2);
                    break;
                case 8:
                    layers.add(6);
                    layers.add(2);
                    break;
            }
            remaining = 0;
        }

        for (int layer = 0; layer < layers.size(); layer++) {
            Integer itemsInLayer = layers.get(layer);
            for (int item = 0; item < itemsInLayer; item++) {
                graph.addNode(layer, item);
            }
        }
    }
}
