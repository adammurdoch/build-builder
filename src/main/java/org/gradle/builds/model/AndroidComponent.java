package org.gradle.builds.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class AndroidComponent extends HasJavaSource {
    private String packageName;
    private final Set<String> stringResources = new LinkedHashSet<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getStringResources() {
        return stringResources;
    }

    public void stringResource(String name) {
        stringResources.add(name);
    }
}
