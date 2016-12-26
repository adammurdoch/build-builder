package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class Project {
    private final BuildScript buildScript = new BuildScript();
    private final Set<Component> components = new LinkedHashSet<>();

    public BuildScript getBuildScript() {
        return buildScript;
    }

    public <T extends Component> T component(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    public <T extends Component> T addComponent(T component) {
        components.add(component);
        return component;
    }
}
