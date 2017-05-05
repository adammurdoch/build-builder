package org.gradle.builds.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class AndroidComponent extends HasJavaSource {
    private String packageName;
    private String labelResource;
    private final Map<String, String> stringResources = new LinkedHashMap<>();
    private final Set<JavaClass> activities = new LinkedHashSet<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabelResource() {
        return labelResource;
    }

    public void setLabelResource(String labelResource) {
        this.labelResource = labelResource;
    }

    public Map<String, String> getStringResources() {
        return stringResources;
    }

    public void stringResource(String name, String value) {
        stringResources.put(name, value);
    }

    public Set<JavaClass> getActivities() {
        return activities;
    }

    public void activity(JavaClass javaClass) {
        activities.add(javaClass);
    }
}
