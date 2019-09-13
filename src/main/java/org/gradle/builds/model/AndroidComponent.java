package org.gradle.builds.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AndroidComponent extends HasJavaSource<JvmLibraryApi> implements HasHeapRequirements {
    private String packageName;
    private String labelResource;
    private final Map<String, String> stringResources = new LinkedHashMap<>();
    private final List<JavaClass> activities = new ArrayList<>();

    public AndroidComponent(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
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

    public List<JavaClass> getActivities() {
        return activities;
    }

    public void activity(JavaClass javaClass) {
        activities.add(javaClass);
    }

    @Override
    public int getMinHeapMegabytes() {
        return 100;
    }
}
