package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlockWithProjectTarget extends BlockWithRepositories {
    private final Set<Plugin> plugins = new LinkedHashSet<>();

    public Set<Plugin> getPlugins() {
        return plugins;
    }

    public void requirePlugin(String id) {
        plugins.add(new Plugin(id, null, null));
    }

    public void requirePlugin(String id, String minVersion) {
        plugins.add(new Plugin(id, minVersion, null));
    }

    public void requirePlugin(String id, String minVersion, String maxVersion) {
        plugins.add(new Plugin(id, minVersion, maxVersion));
    }

    public static class Plugin {
        private final String id;
        private final String minVersion;
        private final String maxVersion;

        Plugin(String id, String minVersion, String maxVersion) {
            this.id = id;
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
        }

        public String getId() {
            return id;
        }

        public String getMinVersion() {
            return minVersion;
        }

        public String getMaxVersion() {
            return maxVersion;
        }
    }
}
