package org.gradle.builds.assemblers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GraphAssembler {
    /**
     * Attempts to create 3 layers, then attempts to keep between 3 and 6 nodes in each layer.
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
        private final Group group;
        private final int item;
        final boolean useAlternate;
        private final List<NodeImpl> dependencies;

        NodeImpl(Group group, int item, List<NodeImpl> dependencies, boolean useAlternate) {
            this.group = group;
            this.item = item;
            this.dependencies = dependencies;
            this.useAlternate = useAlternate;
        }

        @Override
        public String toString() {
            return "{group: " + group + ", item: " + (item + 1) + "}";
        }

        @Override
        public int getLayer() {
            return group.layer.id;
        }

        @Override
        public boolean isDeepest() {
            return group.layer.next == null && item == group.size - 1;
        }

        @Override
        public boolean isUseAlternate() {
            return useAlternate;
        }

        @Override
        public String getNameSuffix() {
            return group.size == 1 ? group.toString() : group.toString() + (item + 1);
        }

        @Override
        public List<? extends Graph.Node> getDependencies() {
            return dependencies;
        }
    }

    private interface Module {
        List<NodeImpl> getProtectedApi();
        List<NodeImpl> getNodes();
    }

    private static class Empty implements Module {
        static final Empty INSTANCE = new Empty();

        @Override
        public List<NodeImpl> getProtectedApi() {
            return Collections.emptyList();
        }

        @Override
        public List<NodeImpl> getNodes() {
            return Collections.emptyList();
        }
    }

    private static class Group implements Module {
        private final Layer layer;
        private final String name;
        private final int size;
        private final List<? extends Module> implementationModules;
        private List<NodeImpl> nodes;

        Group(Layer layer, String name, int size, List<? extends Module> implementationModules) {
            this.layer = layer;
            this.name = name;
            this.size = size;
            this.implementationModules = implementationModules;
        }

        @Override
        public String toString() {
            return layer.id + name;
        }

        @Override
        public List<NodeImpl> getNodes() {
            if (nodes == null) {
                List<NodeImpl> deps = new ArrayList<>();
                for (Module module : implementationModules) {
                    deps.addAll(module.getProtectedApi());
                }
                nodes = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    boolean useAlternate;
                    if (size > 1 && i == size - 1 && layer.canUseAlternate() && canUseAlternate(deps)) {
                        // Last item of this group and all dependencies use alternate
                        useAlternate = true;
                    } else if (size == 1 && layer.canUseAlternate() && deps.isEmpty()) {
                        // Single item group with no dependencies
                        useAlternate = true;
                    } else {
                        useAlternate = false;
                    }
                    this.nodes.add(new NodeImpl(this, i, deps, useAlternate));
                }
            }
            return nodes;
        }

        private boolean canUseAlternate(List<NodeImpl> deps) {
            for (NodeImpl dep : deps) {
                if (!dep.isUseAlternate()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public List<NodeImpl> getProtectedApi() {
            return getNodes();
        }
    }

    private static class Layer implements Module {
        final int id;
        final int size;
        Layer next;
        private List<NodeImpl> nodes;
        private List<NodeImpl> api;

        Layer(int id, int size) {
            this.id = id;
            this.size = size;
        }

        @Override
        public List<NodeImpl> getProtectedApi() {
            maybeBuildNodes();
            return api;
        }

        @Override
        public List<NodeImpl> getNodes() {
            maybeBuildNodes();
            return this.nodes;
        }

        private void maybeBuildNodes() {
            if (this.nodes == null) {
                Module nextLayer = next == null ? Empty.INSTANCE : next;
                this.nodes = new ArrayList<>(size);
                int apiSize = size < 3 ? size : (size + 1) / 2;
                int noDepsSize = (size - apiSize + 1) / 2;
                int implSize = size - apiSize - noDepsSize;
                Module noDeps = new Group(this, "Core", noDepsSize, Collections.emptyList());
                Module impl = new Group(this, "Impl", implSize, Arrays.asList(noDeps, nextLayer));
                Module api = new Group(this, "Api", apiSize, Arrays.asList(impl.getNodes().isEmpty() ? noDeps : impl, nextLayer));
                nodes.addAll(api.getNodes());
                nodes.addAll(impl.getNodes());
                nodes.addAll(noDeps.getNodes());
                this.api = api.getProtectedApi();
            }
        }

        boolean canUseAlternate() {
            return id != 0;
        }
    }
}
