package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProjectScriptBlock extends BlockWithDependencies {
    private final Set<Plugin> plugins = new LinkedHashSet<>();

    public Set<Plugin> getPlugins() {
        return plugins;
    }

    public void requirePlugin(String id) {
        plugins.add(new Plugin(id, null));
    }

    public void requirePlugin(String id, String version) {
        plugins.add(new Plugin(id, version));
    }

    public static class Plugin {
        private final String id;
        private final String version;

        public Plugin(String id, String version) {
            this.id = id;
            this.version = version;
        }

        public String getId() {
            return id;
        }

        public String getVersion() {
            return version;
        }
    }
}
